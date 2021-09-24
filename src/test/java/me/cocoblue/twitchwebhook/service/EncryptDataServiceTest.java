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
        String message = "{\n" +
                "    \"data\": [\n" +
                "        {\n" +
                "            \"game_id\": \"509658\",\n" +
                "            \"game_name\": \"Just Chatting\",\n" +
                "            \"id\": \"abcabc132\",\n" +
                "            \"is_mature\": false,\n" +
                "            \"language\": \"ko\",\n" +
                "            \"started_at\": \"2021-09-13T13:14:47Z\",\n" +
                "            \"tag_ids\": null,\n" +
                "            \"thumbnail_url\": \"https://static-cdn.jtvnw.net/previews-ttv/live_user_soulofcoco-{width}x{height}.jpg\",\n" +
                "            \"title\": \"테스트방송!\",\n" +
                "            \"type\": \"live\",\n" +
                "            \"user_id\": \"672681145\",\n" +
                "            \"user_login\": \"soulofcoco\",\n" +
                "            \"user_name\": \"soulofcoco\",\n" +
                "            \"viewer_count\": 0\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        log.info(encryptDataService.encryptString(message));
    }
}