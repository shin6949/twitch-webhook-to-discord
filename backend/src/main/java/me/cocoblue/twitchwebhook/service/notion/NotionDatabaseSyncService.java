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
import me.cocoblue.twitchwebhook.dto.twitch.eventsub.Subscription;
import me.cocoblue.twitchwebhook.service.twitch.UserInfoService;
import me.cocoblue.twitchwebhook.util.NotionPropertyUtil;
import notion.api.v1.NotionClient;
import notion.api.v1.model.common.OptionColor;
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

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.util.Map.entry;

@Log4j2
@RequiredArgsConstructor
@Service
public class NotionDatabaseSyncService {
    @Value("${twitch.event-renew}")
    private boolean eventEnabled;

    @Value("${notion.api-key:null}")
    private String notionApiKey;

    private final NotionDatabaseIndexRepository notionDatabaseIndexRepository;
    private final SubscriptionFormRepository subscriptionFormRepository;
    private final UserInfoService userInfoService;
    private final BroadcasterIdRepository broadcasterIdRepository;

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

        final List<NotionDatabaseIndexEntity> notionDatabaseIndexEntityList = notionDatabaseIndexRepository.findAll();
        if (notionDatabaseIndexEntityList.isEmpty()) {
            return;
        }

        final NotionClient notionClient = new NotionClient(notionApiKey);

        // 각 Notion Database 마다 같은 과정 수행
        for (final NotionDatabaseIndexEntity notionDatabaseIndex : notionDatabaseIndexEntityList) {
            // 변경 되거나 새롭게 생성한 Item 갖고 옴.
            final List<Page> resultItems = get1HourAgoItems(notionClient, notionDatabaseIndex.getDatabaseIdAtNotion());

        }

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

    private void updateExistItem(final Page page, final NotionClient notionClient,
            final NotionDatabaseIndexEntity notionDatabaseIndex) {
        /*
         * 1. 1시간 내로 변경된 데이터 중, 사람이 편집한 내용을 필터링
         * 판단용 Column의 값이 null이면, Bot이 변경한 것으로 판단하여 제외.
         * 2. 데이터 업데이트
         * 기존 데이터를 갖고 온 뒤, message 또는 color 항목이 변경되었다면 update
         * → Twitch 프로필 부분은 update 시, 참고하지 않음.
         * → 만일, 기존 데이터가 없다면, create 시의 메소드를 따르도록 설정.
         * 3. Notion Database 업데이트
         * - Twitch 프로필이 Database 기준 Profile이 변경되었다면 Update
         * - is_valid를 YES로 변경
         */
        final String targetTwitchUniqueId = NotionPropertyUtil
                .buildStringFromNotionRichText(page.getProperties().get("twitch_unique_id").getRichText());
        if(targetTwitchUniqueId == null) {
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
        final Optional<SubscriptionFormEntity> subscriptionFormEntity = subscriptionFormRepository.getSubscriptionFormEntityByBroadcasterIdEntityAndWebhookIdAndFormOwnerAndTwitchSubscriptionType(
                targetBroadcasterEntity.get(),
                notionDatabaseIndex.getWebhookId(),
                notionDatabaseIndex.getOwnerId(),
                TwitchSubscriptionType.STREAM_ONLINE);
        if(subscriptionFormEntity.isEmpty()) {
            log.info("SubscriptionFormEntity is NOT FOUND. Process as new item.");
            createNewItem(page, notionClient, notionDatabaseIndex);
            return;
        }

        final String SubscriptionMessage = NotionPropertyUtil
                .buildStringFromNotionRichText(page.getProperties().get("message").getRichText());

        if(subscriptionFormEntity.get().getContent().equals(SubscriptionMessage)) {

        }

        final Map<String, PageProperty> result = new HashMap<>();

        // Twitch ID 판단
        Optional.ofNullable(configurePageProperty(page, "twitch_id", targetBroadcasterEntity.get().getLoginId()))
                .ifPresent(pageProperty -> result.put("twitch_id", pageProperty));

        // Twitch Nickname 판단
        Optional.ofNullable(configurePageProperty(page, "nickname", targetBroadcasterEntity.get().getDisplayName()))
                .ifPresent(pageProperty -> result.put("nickname", pageProperty));

        // is_valid YES로 변경
        result.put("is_valid", new PageProperty(
                page.getProperties().get("is_valid").getId(),
                PropertyType.Select,
                null,
                null,
                new DatabaseProperty.Select.Option(null, "YES", null)));

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
     * 주어진 Twitch ID가 존재하는지 확인합니다.
     * 
     * @param targetTwitchId 확인할 Twitch ID
     * @return Twitch ID가 존재하는 경우 true, 그렇지 않은 경우 false를 반환합니다.
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
}
