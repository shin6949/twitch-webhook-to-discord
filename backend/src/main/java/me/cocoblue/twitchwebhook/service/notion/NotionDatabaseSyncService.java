package me.cocoblue.twitchwebhook.service.notion;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.domain.notion.NotionDatabaseIndexEntity;
import me.cocoblue.twitchwebhook.domain.notion.NotionDatabaseIndexRepository;
import me.cocoblue.twitchwebhook.domain.twitch.BroadcasterIdEntity;
import me.cocoblue.twitchwebhook.domain.twitch.BroadcasterIdRepository;
import me.cocoblue.twitchwebhook.dto.twitch.User;
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
        if(notionDatabaseIndexEntityList.isEmpty()) {
            return;
        }

        final NotionClient notionClient = new NotionClient(notionApiKey);
        for(final NotionDatabaseIndexEntity notionDatabaseIndex : notionDatabaseIndexEntityList) {
            final List<Page> resultItems = get1HourAgoItems(notionClient, notionDatabaseIndex.getDatabaseIdAtNotion());
        }

        notionClient.close();
    }

    private void savePageToDatabase(final List<Page> resultPagesList, final NotionClient notionClient) {
        for(final Page page : resultPagesList) {
            final ZonedDateTime pageCreatedAt = ZonedDateTime.ofInstant(Instant.parse(page.getCreatedTime()), ZoneId.of("UTC"));
            final ZonedDateTime oneHourAgo = ZonedDateTime.of(LocalDateTime.now().minusHours(1), ZoneId.of("UTC"));

            // 신규 등록인 경우 User 정보 검증 필요.
            if(pageCreatedAt.isAfter(oneHourAgo)) {
                if(page.getProperties().get("twitch_id").getRichText() == null) {
                    addInvalidMessageToPage(notionClient, page, "요청한 유저가 존재하지 않습니다.");
                    continue;
                }

                final String targetTwitchId = NotionPropertyUtil.buildStringFromNotionRichText(page.getProperties().get("twitch_id").getRichText());
                final boolean isUserExists = isTwitchUserExists(targetTwitchId);
                // 없는 경우 예외 처리 및 다음 item 진행
                if(!isUserExists) {
                    addInvalidMessageToPage(notionClient, page, "요청한 유저가 존재하지 않습니다.");
                    continue;
                }
            }


        }

    }

    /**
     * 특정 Notion Page에서 Update 해야할 데이터가 있는지 검사하고, UpdatePageRequest를 구성하는 메소드
     *
     * @param page 업데이트 해야할지 검사할 Data가 있는 Page 객체
     */
    private UpdatePageRequest configureUpdatePageRequest(final Page page, final BroadcasterIdEntity targetBroadcasterEntity) {
        Map<String, PageProperty> result = new HashMap<>();

        // Twitch Unique ID 판단
        Optional.ofNullable(configurePageProperty(page, "twitch_unique_id", targetBroadcasterEntity.getId().toString()))
                .ifPresent(pageProperty -> result.put("twitch_unique_id", pageProperty));

        // Twitch ID 판단
        Optional.ofNullable(configurePageProperty(page, "twitch_id", targetBroadcasterEntity.getLoginId()))
                .ifPresent(pageProperty -> result.put("twitch_id", pageProperty));

        // Twitch Nickname 판단
        Optional.ofNullable(configurePageProperty(page, "nickname", targetBroadcasterEntity.getDisplayName()))
                .ifPresent(pageProperty -> result.put("nickname", pageProperty));

        // is_valid YES로 변경
        result.put("is_valid", new PageProperty(
                page.getProperties().get("is_valid").getId(),
                PropertyType.Select,
                null,
                null,
                new DatabaseProperty.Select.Option(null, "YES", null))
        );

        return new UpdatePageRequest(page.getId(), result);
    }

    /**
     * 특정 Column이 같은지 판단하고, Update를 위한 값을 구성해주는 메소드. RichText만 지원.
     *
     * @param page Column이 포함된 Page 객체
     * @param targetColumnName 데이터를 비교할 Column Name
     * @return 다르면 Map<String, PageProperty>, 같으면 null
     */
    private PageProperty configurePageProperty(final Page page, final String targetColumnName, final String toCompareValue) {
        final String targetColumnValue = NotionPropertyUtil.buildStringFromNotionRichText(page.getProperties().get(targetColumnName).getRichText());
        if(targetColumnValue == null || !targetColumnValue.equals(toCompareValue)) {
            return new PageProperty(
                    page.getProperties().get(targetColumnName).getId(),
                    PropertyType.RichText,
                    null,
                    Collections.singletonList(new PageProperty.RichText(RichTextType.Text,
                            new PageProperty.RichText.Text(toCompareValue, null)))
            );
        } else {
            // 값이 이전과 같은 경우
            return null;
        }
    }

    private boolean isTwitchUserExists(final String targetTwitchId) {
        final Optional<BroadcasterIdEntity> broadcasterIdEntity = broadcasterIdRepository.getBroadcasterIdEntityByLoginIdEquals(targetTwitchId);
        if(broadcasterIdEntity.isEmpty()) {
            final Optional<User> user = userInfoService.getUserInfoByLoginIdFromTwitch(targetTwitchId);
            return user.isPresent();
        }

        return true;
    }

    private void addInvalidMessageToPage(final NotionClient notionClient, final Page page, final String invalidMessage) {
        final String resultMessage = NotionPropertyUtil.buildStringFromNotionRichText(page.getProperties().get("invalid_reason").getRichText()) +
                "\n" + invalidMessage;

        final UpdatePageRequest updatePageRequest = new UpdatePageRequest(
                page.getId(),
                Map.ofEntries(entry("invalid_reason", new PageProperty(
                        page.getProperties().get("invalid_reason").getId(),
                        PropertyType.RichText,
                        null,
                        Collections.singletonList(new PageProperty.RichText(RichTextType.Text,
                                new PageProperty.RichText.Text(resultMessage, null)))
                )))
        );

        notionClient.updatePage(updatePageRequest);
    }

    private List<Page> get1HourAgoItems(final NotionClient notionClient, final String databaseId) {
        final String oneHourAgoString = ZonedDateTime.now(ZoneId.of("UTC")).minusHours(1).format(DateTimeFormatter.ISO_INSTANT);
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
