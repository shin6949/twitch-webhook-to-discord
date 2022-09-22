package me.cocoblue.twitchwebhook.service;

import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.dto.twitch.eventsub.Subscription;
import me.cocoblue.twitchwebhook.service.twitch.EventSubService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@Log4j2
public class EventSubServiceTests {
    @Autowired
    private EventSubService eventSubService;

    @Autowired
    private OauthTokenService oauthTokenService;

    @Test
    public void getSubscriptionListTest() {
        final String accessToken = oauthTokenService.getAppTokenFromTwitch().getAccessToken();
        log.info(eventSubService.getSubscriptionListFromTwitch(accessToken).toString());
    }

    @Test
    public void deleteSubscriptionTest() {
        eventSubService.deleteEventSub("9818218a-441d-4b69-8891-5f042450299b");
    }

    @Test
    public void deleteAllSubscription() {
        final String accessToken = oauthTokenService.getAppTokenFromTwitch().getAccessToken();
        final List<Subscription> subscriptionListFromTwitch = eventSubService.getSubscriptionListFromTwitch(accessToken).getSubscriptionList();

        for(Subscription subscription: subscriptionListFromTwitch) {
            eventSubService.deleteEventSub(subscription.getId(), accessToken);
        }

        oauthTokenService.revokeAppTokenToTwitch(accessToken);
    }
}
