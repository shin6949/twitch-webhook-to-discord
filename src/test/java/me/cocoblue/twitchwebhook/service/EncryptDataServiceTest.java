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
                "    \"subscription\": {\n" +
                "        \"id\": \"f1c2a387-161a-49f9-a165-0f21d7a4e1c4\",\n" +
                "        \"type\": \"stream.online\",\n" +
                "        \"version\": \"1\",\n" +
                "        \"status\": \"enabled\",\n" +
                "        \"cost\": 0,\n" +
                "        \"condition\": {\n" +
                "            \"broadcaster_user_id\": \"672681145\"\n" +
                "        },\n" +
                "         \"transport\": {\n" +
                "            \"method\": \"webhook\",\n" +
                "            \"callback\": \"https://cocoblueoverlay.azurewebsites.net/webhook/stream/672681145/online\"\n" +
                "        },\n" +
                "        \"created_at\": \"2019-11-16T10:11:12.123Z\"\n" +
                "    },\n" +
                "    \"event\": {\n" +
                "        \"id\": \"9001\",\n" +
                "        \"broadcaster_user_id\": \"672681145\",\n" +
                "        \"broadcaster_user_login\": \"soulofcoco\",\n" +
                "        \"broadcaster_user_name\": \"soulofcoco\",\n" +
                "        \"type\": \"live\",\n" +
                "        \"started_at\": \"2021-12-23T10:11:12.123Z\"\n" +
                "    }\n" +
                "}";

        log.info("Encrypt Data: " + encryptDataService.encryptString(message, true));
    }
}