package me.cocoblue.twitchwebhook.service.notion;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.domain.notion.NotionDatabaseIndexEntity;
import me.cocoblue.twitchwebhook.domain.notion.NotionDatabaseIndexRepository;
import notion.api.v1.NotionClient;
import notion.api.v1.model.databases.Database;
import notion.api.v1.model.databases.QueryResults;
import notion.api.v1.model.databases.query.filter.CompoundFilter;
import notion.api.v1.model.databases.query.filter.PropertyFilter;
import notion.api.v1.model.databases.query.filter.QueryTopLevelFilter;
import notion.api.v1.model.databases.query.filter.condition.DateFilter;
import notion.api.v1.model.databases.query.filter.condition.TextFilter;
import notion.api.v1.request.databases.QueryDatabaseRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Log4j2
@RequiredArgsConstructor
@Service
public class NotionDatabaseSyncService {
    @Value("${twitch.event-renew}")
    private boolean eventEnabled;

    @Value("${notion.api-key:null}")
    private String notionApiKey;

    private final NotionDatabaseIndexRepository notionDatabaseIndexRepository;


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
        for(NotionDatabaseIndexEntity notionDatabaseIndex : notionDatabaseIndexEntityList) {
            final String oneHourAgo = ZonedDateTime.now(ZoneId.of("UTC")).minusHours(1).format(DateTimeFormatter.ISO_INSTANT);
            final DateFilter updatedAtDateFilter = new DateFilter();
            updatedAtDateFilter.setAfter(oneHourAgo);

            final PropertyFilter updatedAtFilter = new PropertyFilter();
            updatedAtFilter.setProperty("updated_at");
            updatedAtFilter.setDate(updatedAtDateFilter);

            final TextFilter textFilter = new TextFilter();
            textFilter.setNotEmpty(true);

            final PropertyFilter lastEditorStringFilter = new PropertyFilter();
            lastEditorStringFilter.setProperty("last_editor_string");
            lastEditorStringFilter.setRichText(textFilter);

            // filter를 List 형태로 모은 뒤, and 조건에 지정
            final List<PropertyFilter> filters = Arrays.asList(lastEditorStringFilter, updatedAtFilter);
            final CompoundFilter compoundFilter = new CompoundFilter(null, filters);

            final QueryDatabaseRequest queryDatabaseRequest = new QueryDatabaseRequest(notionDatabaseIndex.getDatabaseIdAtNotion(), compoundFilter);

            QueryResults queryResults = notionClient.queryDatabase(queryDatabaseRequest);

        }

        notionClient.close();
    }

    private void databaseSync(final NotionClient notionClient, final NotionDatabaseIndexEntity notionDatabaseIndex) {

    }
}
