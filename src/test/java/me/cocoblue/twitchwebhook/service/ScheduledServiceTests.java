package me.cocoblue.twitchwebhook.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ScheduledServiceTests {
    @Autowired
    private ScheduledService scheduledService;

    @Test
    void eventSubscriptionCheckTest() {
        scheduledService.eventSubscriptionCheck();
    }
}
