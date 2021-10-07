package me.cocoblue.twitchwebhook.service;

import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.service.twitch.SubscriptionListService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Log4j2
public class SubscriptionListServiceTests {
    @Autowired
    private SubscriptionListService subscriptionListService;

    @Test
    public void  getSubscriptionListTest() {
        log.info(subscriptionListService.getSubscriptionListFromTwitch().toString());
    }
}
