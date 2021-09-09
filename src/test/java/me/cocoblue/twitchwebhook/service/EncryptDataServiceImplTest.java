package me.cocoblue.twitchwebhook.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Log4j2
class EncryptDataServiceImplTest {
    @Autowired
    private EncryptDataServiceImpl encryptDataService;

    @Test
    public void test() {
        String message = "{\n" +
                "  \"data\": [\n" +
                "    {\n" +
                "      \"id\": \"3d141868-46ed-4ef8-cd09-1fbb4f84535\",\n" +
                "      \"user_id\": \"464457053\",\n" +
                "      \"user_login\": \"testBroadcaster\",\n" +
                "      \"user_name\": \"testBroadcaster\",\n" +
                "      \"game_id\": \"495064\",\n" +
                "      \"game_name\": \"Splatoon 2\",\n" +
                "      \"type\": \"live\",\n" +
                "      \"title\": \"Example title from the CLI!\",\n" +
                "      \"viewer_count\": 9848,\n" +
                "      \"started_at\": \"2021-03-20T03:18:50Z\",\n" +
                "      \"language\": \"ko\",\n" +
                "      \"thumbnail_url\": \"https://static-cdn.jtvnw.net/previews-ttv/live_twitch_user-{width}x{height}.jpg\",\n" +
                "      \"tag_ids\": []\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        log.info(encryptDataService.encryptString(message));
    }
}