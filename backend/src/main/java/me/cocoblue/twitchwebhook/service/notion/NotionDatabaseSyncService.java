package me.cocoblue.twitchwebhook.service.notion;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.domain.notion.NotionDatabaseIndexEntity;
import me.cocoblue.twitchwebhook.domain.notion.NotionDatabaseIndexRepository;
import notion.api.v1.NotionClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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
            notionClient.queryDatabase()
        }

        notionClient.close();
    }

    private void databaseSync() {

    }
}
