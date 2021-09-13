package me.cocoblue.twitchwebhook.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Log4j2
class EncryptDataServiceTest {
    @Autowired
    private EncryptDataService encryptDataService;

    @Test
    public void test() {
        String message = "{\"data\":[{\"game_id\":\"509658\",\"game_name\":\"Just Chatting\",\"id\":\"43677839309\",\"is_mature\":false,\"language\":\"ko\",\"started_at\":\"2021-09-13T13:14:47Z\",\"tag_ids\":null,\"thumbnail_url\":\"https://static-cdn.jtvnw.net/previews-ttv/live_user_soulofcoco-{width}x{height}.jpg\",\"title\":\"테스트방송!\",\"type\":\"live\",\"user_id\":\"672681145\",\"user_login\":\"soulofcoco\",\"user_name\":\"soulofcoco\",\"viewer_count\":0}]}";

        log.info(encryptDataService.encryptString(message));
    }
}