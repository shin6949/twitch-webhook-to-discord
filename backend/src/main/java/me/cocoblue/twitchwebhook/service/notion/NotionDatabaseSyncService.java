package me.cocoblue.twitchwebhook.service.notion;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.data.TwitchSubscriptionType;
import me.cocoblue.twitchwebhook.domain.discord.SubscriptionFormEntity;
import me.cocoblue.twitchwebhook.domain.discord.SubscriptionFormRepository;
import me.cocoblue.twitchwebhook.domain.notion.NotionDatabaseIndexEntity;
import me.cocoblue.twitchwebhook.domain.notion.NotionDatabaseIndexRepository;
import me.cocoblue.twitchwebhook.domain.twitch.BroadcasterIdEntity;
import me.cocoblue.twitchwebhook.domain.twitch.BroadcasterIdRepository;
import me.cocoblue.twitchwebhook.dto.twitch.User;
import me.cocoblue.twitchwebhook.service.twitch.UserInfoService;
import me.cocoblue.twitchwebhook.util.NotionPropertyUtil;
import notion.api.v1.NotionClient;
import notion.api.v1.model.common.PropertyType;
import notion.api.v1.model.common.RichTextType;
import notion.api.v1.model.databases.DatabaseProperty;
import notion.api.v1.model.databases.QueryResults;
import notion.api.v1.model.databases.query.filter.CompoundFilter;
import notion.api.v1.model.databases.query.filter.CompoundFilterElement;
import notion.api.v1.model.databases.query.filter.PropertyFilter;
import notion.api.v1.model.databases.query.filter.condition.DateFilter;
import notion.api.v1.model.databases.query.filter.condition.TextFilter;
import notion.api.v1.model.pages.Page;
import notion.api.v1.model.pages.PageProperty;
import notion.api.v1.request.databases.QueryDatabaseRequest;
import notion.api.v1.request.pages.UpdatePageRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.util.Map.entry;

@Log4j2
@RequiredArgsConstructor
@Service
public class NotionDatabaseSyncService {
    private final NotionDatabaseIndexRepository notionDatabaseIndexRepository;
    private final SubscriptionFormRepository subscriptionFormRepository;
    private final UserInfoService userInfoService;
    private final BroadcasterIdRepository broadcasterIdRepository;
    @Value("${twitch.event-renew}")
    private boolean eventEnabled;
    @Value("${notion.api-key:null}")
    private String notionApiKey;

    /**
     * 변경된 내용만 추적하는 Job
     * 매시 28분에 실행 됨.
     */
    @Scheduled(cron = "0 28 */1 * * *")
    public void notionDatabaseIncrementSyncJob() {
        if (!eventEnabled) {
            log.info("Event Renew Function Disabled. Do Not Processing.");
            return;
        }

        if (notionApiKey.equals("null")) {
            log.error("Notion API Key is not defined.");
            return;
        }

        // Notion API Client 생성
        final NotionClient notionClient = new NotionClient(notionApiKey);

        // Notion Database Index Table에서 모든 Index 가져오기
        final List<NotionDatabaseIndexEntity> notionDatabaseIndexEntityList = notionDatabaseIndexRepository.findAll();
        if (notionDatabaseIndexEntityList.isEmpty()) {
            return;
        }

        // 각 Notion Database 마다 같은 과정 수행
        for (final NotionDatabaseIndexEntity notionDatabaseIndex : notionDatabaseIndexEntityList) {
            // 변경 되거나 새롭게 생성한 Item 갖고 옴.
            final List<Page> resultItems = get1HourAgoItems(notionClient, notionDatabaseIndex.getDatabaseIdAtNotion());

            processNotionDatabase(resultItems, notionClient, notionDatabaseIndex);
            arrangeDatabaseItem(notionClient, notionDatabaseIndex);
        }

        // Notion API Client 종료
        notionClient.close();
    }

    private void createNewItem(final Page page, final NotionClient notionClient,
                               final NotionDatabaseIndexEntity notionDatabaseIndex) {
        if (page.getProperties().get("twitch_id").getRichText() == null) {
            addInvalidMessageToPage(notionClient, page, "요청한 유저가 존재하지 않습니다.");
            return;
        }

        final String targetTwitchId = NotionPropertyUtil
                .buildStringFromNotionRichText(page.getProperties().get("twitch_id").getRichText());
        final boolean isUserExists = isTwitchUserExists(targetTwitchId);
        // 없는 경우 예외 처리 및 다음 item 진행
        if (!isUserExists) {
            addInvalidMessageToPage(notionClient, page, "요청한 유저가 존재하지 않습니다.");
            return;
        }

        // isTwitchUserExists를 실행했기 때문에 있을 것이라고 기대됨.
        final Optional<BroadcasterIdEntity> broadcasterIdEntity = broadcasterIdRepository
                .getBroadcasterIdEntityByLoginIdEquals(targetTwitchId);
        if (broadcasterIdEntity.isEmpty()) {
            addInvalidMessageToPage(notionClient, page, "요청한 유저가 존재하지 않습니다.");
            return;
        }

        final String message = NotionPropertyUtil
                .buildStringFromNotionRichText(page.getProperties().get("message").getRichText());
        final String colorString = NotionPropertyUtil
                .buildStringFromNotionRichText(page.getProperties().get("color").getRichText());

        // 입력한 데이터를 Database에 저장
        final SubscriptionFormEntity subscriptionFormEntity = SubscriptionFormEntity.builder()
                .formOwner(notionDatabaseIndex.getOwnerId())
                .botProfileId(notionDatabaseIndex.getProfileId())
                .webhookId(notionDatabaseIndex.getWebhookId())
                .colorHex(isColorHex(colorString) ? colorString : notionDatabaseIndex.getDefaultColorHex())
                .content(message)
                .languageIsoData(notionDatabaseIndex.getLanguageIsoData())
                .twitchSubscriptionType(TwitchSubscriptionType.STREAM_ONLINE)
                .intervalMinute(notionDatabaseIndex.getIntervalMinute())
                .enabled(false)
                .build();

        subscriptionFormRepository.save(subscriptionFormEntity);
    }

    /**
     * 변경된 데이터를 Database에 Insert / Update 하는 메소드
     *
     * @param resultPagesList 업데이트 해야할 Notion Page 들
     * @param notionClient    NotionClient 객체
     */
    private void processNotionDatabase(final List<Page> resultPagesList, final NotionClient notionClient,
                                       final NotionDatabaseIndexEntity notionDatabaseIndex) {
        for (final Page page : resultPagesList) {
            final ZonedDateTime pageCreatedAt = ZonedDateTime.ofInstant(Instant.parse(page.getCreatedTime()),
                    ZoneId.of("UTC"));
            final ZonedDateTime oneHourAgo = ZonedDateTime.of(LocalDateTime.now().minusHours(1), ZoneId.of("UTC"));

            // 신규 데이터인 경우
            if (pageCreatedAt.isAfter(oneHourAgo)) {
                createNewItem(page, notionClient, notionDatabaseIndex);
                continue;
            }

            // 변경된 데이터의 경우
            updateExistItem(page, notionClient, notionDatabaseIndex);
        }
    }

    /**
     * 1시간 내로 변경된 데이터 중, 사람이 편집한 내용을 필터링하여 DB, Notion Database에 업데이트하는 메소드
     *
     * @param page Notion Page
     * @param notionClient NotionClient 객체
     * @param notionDatabaseIndex Notion Database Index
     */
    private void updateExistItem(final Page page, final NotionClient notionClient,
                                 final NotionDatabaseIndexEntity notionDatabaseIndex) {
        final String targetTwitchUniqueId = NotionPropertyUtil
                .buildStringFromNotionRichText(page.getProperties().get("twitch_unique_id").getRichText());
        if (targetTwitchUniqueId == null) {
            log.info("twitch_unique_id is NOT FOUND. Process as new item.");
            createNewItem(page, notionClient, notionDatabaseIndex);
            return;
        }

        // 기등록된 인원이기에 Database에 있을 것이라고 기대되어 Twitch에 요청하지 않고, Database에서만 가져옴.
        final Optional<BroadcasterIdEntity> targetBroadcasterEntity = broadcasterIdRepository
                .getBroadcasterIdEntityByIdEquals(Long.parseLong(targetTwitchUniqueId));
        if (targetBroadcasterEntity.isEmpty()) {
            addInvalidMessageToPage(notionClient, page, "요청한 유저가 존재하지 않습니다.");
            return;
        }

        // message가 변경되었는지 판단.
        final Optional<SubscriptionFormEntity> subscriptionFormEntity = subscriptionFormRepository
                .getSubscriptionFormEntityByBroadcasterIdEntityAndWebhookIdAndFormOwnerAndTwitchSubscriptionType(
                        targetBroadcasterEntity.get(),
                        notionDatabaseIndex.getWebhookId(),
                        notionDatabaseIndex.getOwnerId(),
                        TwitchSubscriptionType.STREAM_ONLINE);

        if (subscriptionFormEntity.isEmpty()) {
            log.info("SubscriptionFormEntity is NOT FOUND. Process as new item.");
            createNewItem(page, notionClient, notionDatabaseIndex);
            return;
        }

        final String subscriptionMessageFromNotion = NotionPropertyUtil
                .buildStringFromNotionRichText(page.getProperties().get("message").getRichText());
        String colorValueFromNotion = NotionPropertyUtil
                .buildStringFromNotionRichText(page.getProperties().get("color").getRichText());

        final Map<String, PageProperty> result = new HashMap<>();

        // Message 또는 Color가 변경되었는지 판단
        if (!subscriptionFormEntity.get().getContent().equals(subscriptionMessageFromNotion) ||
                !subscriptionFormEntity.get().getColorHex().equals(colorValueFromNotion)) {
            log.info("There is a change in the subscription. Update the subscription to database.");
            // 변경된 경우, Database에 Update
            subscriptionFormEntity.get().setContent(subscriptionMessageFromNotion);

            if (!isColorHex(colorValueFromNotion)) {
                log.info("Color is not Hex. Process as default color.");
                colorValueFromNotion = notionDatabaseIndex.getDefaultColorHex();
                addInvalidMessageToPage(notionClient, page, "색상 값이 올바르지 않습니다. 기본 색상으로 변경합니다.");
                result.put("color", new PageProperty(
                        page.getProperties().get("color").getId(),
                        PropertyType.RichText,
                        null,
                        Collections.singletonList(new PageProperty.RichText(RichTextType.Text,
                                new PageProperty.RichText.Text(notionDatabaseIndex.getDefaultColorHex(), null)))));
            }
            subscriptionFormEntity.get().setColorHex(colorValueFromNotion);

            subscriptionFormRepository.save(subscriptionFormEntity.get());
            log.info("The subscription who owned {} is updated. Target twitch user is {}",
                    notionDatabaseIndex.getOwnerId().getLoginId(),
                    targetBroadcasterEntity.get().getLoginId());
        }

        // Twitch ID 판단
        Optional.ofNullable(configurePageProperty(page, "twitch_id", targetBroadcasterEntity.get().getLoginId()))
                .ifPresent(pageProperty -> result.put("twitch_id", pageProperty));

        // Twitch Nickname 판단
        Optional.ofNullable(configurePageProperty(page, "nickname", targetBroadcasterEntity.get().getDisplayName()))
                .ifPresent(pageProperty -> result.put("nickname", pageProperty));

        // ColorHex가 유효하지 않으면 상기에서 처리했기에 여기서는 유효한 경우만 처리
        if (isColorHex(colorValueFromNotion)) {
            // is_valid YES로 변경
            result.put("is_valid", new PageProperty(
                    page.getProperties().get("is_valid").getId(),
                    PropertyType.Select,
                    null,
                    null,
                    new DatabaseProperty.Select.Option(null, "YES", null)));
        }

        final UpdatePageRequest updatePageRequest = new UpdatePageRequest(page.getId(), result);
        notionClient.updatePage(updatePageRequest);
    }

    /**
     * 특정 Column이 같은지 판단하고, Update를 위한 값을 구성해주는 메소드. RichText만 지원.
     *
     * @param page             Column이 포함된 Page 객체
     * @param targetColumnName 데이터를 비교할 Column Name
     * @return 다르면 Map<String, PageProperty>, 같으면 null
     */
    private PageProperty configurePageProperty(final Page page, final String targetColumnName,
                                               final String toCompareValue) {
        final String targetColumnValue = NotionPropertyUtil
                .buildStringFromNotionRichText(page.getProperties().get(targetColumnName).getRichText());
        if (targetColumnValue == null || !targetColumnValue.equals(toCompareValue)) {
            return new PageProperty(
                    page.getProperties().get(targetColumnName).getId(),
                    PropertyType.RichText,
                    null,
                    Collections.singletonList(new PageProperty.RichText(RichTextType.Text,
                            new PageProperty.RichText.Text(toCompareValue, null))));
        } else {
            // 값이 이전과 같은 경우
            return null;
        }
    }

    /**
     * 주어진 Twitch ID가 존재하는지 확인하는 메소드.
     *
     * @param targetTwitchId 확인할 Twitch ID
     * @return Twitch ID가 존재하는 경우 true, 그렇지 않은 경우 false를 반환.
     */
    private boolean isTwitchUserExists(final String targetTwitchId) {
        final Optional<BroadcasterIdEntity> broadcasterIdEntity = broadcasterIdRepository
                .getBroadcasterIdEntityByLoginIdEquals(targetTwitchId);
        if (broadcasterIdEntity.isEmpty()) {
            final Optional<User> user = userInfoService.getUserInfoByLoginIdFromTwitch(targetTwitchId);
            return user.isPresent();
        }

        return true;
    }

    /**
     * Page에 Invalid Message를 추가하는 메소드
     *
     * @param notionClient   NotionClient 객체
     * @param page           Invalid Message를 추가할 Page 객체
     * @param invalidMessage 추가할 Invalid Message
     */
    private void addInvalidMessageToPage(final NotionClient notionClient, final Page page,
                                         final String invalidMessage) {
        final String resultMessage = NotionPropertyUtil
                .buildStringFromNotionRichText(page.getProperties().get("invalid_reason").getRichText()) +
                "\n" + invalidMessage;

        final UpdatePageRequest updatePageRequest = new UpdatePageRequest(
                page.getId(),
                Map.ofEntries(entry("invalid_reason", new PageProperty(
                        page.getProperties().get("invalid_reason").getId(),
                        PropertyType.RichText,
                        null,
                        Collections.singletonList(new PageProperty.RichText(RichTextType.Text,
                                new PageProperty.RichText.Text(resultMessage, null)))))));

        notionClient.updatePage(updatePageRequest);
    }

    /**
     * 주어진 String이 Color Hex 값인지 확인합니다.
     *
     * @param colorHex 확인할 String
     * @return Color Hex 값인 경우 true, 그렇지 않은 경우 false를 반환합니다.
     */
    private boolean isColorHex(final String colorHex) {
        if (colorHex == null)
            return false;
        if (colorHex.isEmpty())
            return false;

        final String pattern = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$";
        return colorHex.matches(pattern);
    }

    /**
     * Notion Database에서 1시간 이내에 변경된 Item들을 갖고 옵니다.
     *
     * @param notionClient NotionClient 객체
     * @param databaseId   Notion Database ID
     * @return 1시간 이내에 변경된 Item들
     */
    private List<Page> get1HourAgoItems(final NotionClient notionClient, final String databaseId) {
        final String oneHourAgoString = ZonedDateTime.now(ZoneId.of("UTC")).minusHours(1)
                .format(DateTimeFormatter.ISO_INSTANT);
        final DateFilter updatedAtDateFilter = new DateFilter();
        updatedAtDateFilter.setAfter(oneHourAgoString);

        final PropertyFilter updatedAtFilter = new PropertyFilter();
        updatedAtFilter.setProperty("updated_at");
        updatedAtFilter.setDate(updatedAtDateFilter);

        final TextFilter textFilter = new TextFilter();
        // 이 값은 무조건 true로만 지정 가능, 반대 상황을 지정하고 싶다면 setEmpty를 사용할 것.
        textFilter.setNotEmpty(true);

        final PropertyFilter lastEditorStringFilter = new PropertyFilter();
        lastEditorStringFilter.setProperty("last_editor_string");
        lastEditorStringFilter.setRichText(textFilter);

        // filter를 List 형태로 모은 뒤, and 조건에 지정
        final List<CompoundFilterElement> filters = Arrays.asList(lastEditorStringFilter, updatedAtFilter);
        final CompoundFilter compoundFilter = new CompoundFilter();
        compoundFilter.setAnd(filters);

        final QueryDatabaseRequest queryDatabaseRequest = new QueryDatabaseRequest(databaseId, compoundFilter);
        QueryResults queryResults = notionClient.queryDatabase(queryDatabaseRequest);
        final List<Page> allResults = new ArrayList<>(queryResults.getResults());
        while (queryResults.getHasMore()) {
            final QueryDatabaseRequest nextRequest = new QueryDatabaseRequest(databaseId, compoundFilter);
            nextRequest.setStartCursor(queryResults.getNextCursor());
            queryResults = notionClient.queryDatabase(nextRequest);

            allResults.addAll(queryResults.getResults());
        }

        return allResults;
    }

    /**
     * Notion Database에서 모든 값을 갖고 와서 DB와 비교하여 Notion에서 행이 삭제되었다면, DB에서도 삭제하는 메소드
     *
     * @param notionClient        NotionClient 객체
     * @param notionDatabaseIndex Notion Database Index
     */
    private void arrangeDatabaseItem(final NotionClient notionClient,
                                          final NotionDatabaseIndexEntity notionDatabaseIndex) {
        /*
         * 구현 전략
         * 1. databaseId를 이용하여 Notion Database에서 모든 행을 갖고 옴.
         * 2. NotionDatabaseIndexEntity에서 ownerId, webhookId, profileId를 갖고 옴.
         * 3. NotionDatabaseIndexEntity에서 ownerId, webhookId, profileId를 기반으로
         * SubscriptionFormRepository에서 모든 행을 갖고 옴.
         * 4. 1번에서 갖고 온 행들을 4번에서 갖고 온 행들과 비교하여, 4번에는 있지만 1번에는 없는 행을 찾아서 삭제.
         */
        final QueryDatabaseRequest queryDatabaseRequest = new QueryDatabaseRequest(
                notionDatabaseIndex.getDatabaseIdAtNotion());
        QueryResults queryResults = notionClient.queryDatabase(queryDatabaseRequest);
        final List<Page> allResults = new ArrayList<>(queryResults.getResults());
        while (queryResults.getHasMore()) {
            final QueryDatabaseRequest nextRequest = new QueryDatabaseRequest(
                    notionDatabaseIndex.getDatabaseIdAtNotion());
            nextRequest.setStartCursor(queryResults.getNextCursor());
            queryResults = notionClient.queryDatabase(nextRequest);

            allResults.addAll(queryResults.getResults());
        }

        final List<SubscriptionFormEntity> subscriptionFormEntityList = subscriptionFormRepository
                .getSubscriptionFormEntitiesByWebhookIdAndFormOwnerAndTwitchSubscriptionType(
                        notionDatabaseIndex.getWebhookId(),
                        notionDatabaseIndex.getOwnerId(),
                        TwitchSubscriptionType.STREAM_ONLINE);

        for (final SubscriptionFormEntity subscriptionFormEntity : subscriptionFormEntityList) {
            final Optional<Page> page = allResults.stream()
                    .filter(e -> {
                        final String twitchUniqueId = NotionPropertyUtil
                                .buildStringFromNotionRichText(e.getProperties().get("twitch_unique_id").getRichText());
                        if (twitchUniqueId == null)
                            return false;

                        return twitchUniqueId
                                .equals(String.valueOf(subscriptionFormEntity.getBroadcasterIdEntity().getId()));
                    })
                    .findFirst();

            if (page.isEmpty()) {
                log.info("Page is NOT FOUND. Delete subscription form. {}", subscriptionFormEntity);
                subscriptionFormRepository.delete(subscriptionFormEntity);
            }
        }
    }

}
