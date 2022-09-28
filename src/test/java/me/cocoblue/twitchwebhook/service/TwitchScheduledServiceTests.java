package me.cocoblue.twitchwebhook.service;

import me.cocoblue.twitchwebhook.service.twitch.TwitchScheduledService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TwitchScheduledServiceTests {
    @Autowired
    private TwitchScheduledService twitchScheduledService;

    @Test
    void eventSubscriptionCheckTest() {
        twitchScheduledService.eventSubscriptionCheck();
    }
}
