package me.cocoblue.twitchwebhook.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Log4j2
class WebhookRenewServiceTests {
    @Autowired
    private WebhookRenewService webhookRenewService;

    @Test
    public void getStartFormByBroadcasterIdAndTypeTest() {
        webhookRenewService.RenewCronjob();
    }
}
