package me.cocoblue.twitchwebhook.service;

import me.cocoblue.twitchwebhook.service.twitch.ScheduledService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class YouTubeScheduledServiceTests {
    @Autowired
    private ScheduledService scheduledService;

    @Test
    void eventSubscriptionCheckTest() {
        scheduledService.eventSubscriptionCheck();
    }
}
