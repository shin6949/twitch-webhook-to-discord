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
        String message = "{\"subscription\":{\"id\":\"de67eff0-68da-47fe-8508-1d0780d37ed1\",\"status\":\"webhook_callback_verification_pending\",\"type\":\"stream.online\",\"version\":\"1\",\"condition\":{\"broadcaster_user_id\":\"268907830\"},\"transport\":{\"method\":\"webhook\",\"callback\":\"https://deaa-115-178-87-144.ngrok.io/webhook/stream/268907830/online\"},\"created_at\":\"2021-10-06T02:47:37.846672392Z\",\"cost\":1},\"challenge\":\"a4FWlR_4jzn2Da2m6GAxvcwWWflBab4ED7QxtjCcXP4\"}";

        log.info(encryptDataService.encryptString(message));
    }
}