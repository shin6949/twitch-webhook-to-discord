package me.cocoblue.twitchwebhook.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.service.twitch.EventSubService;
import me.cocoblue.twitchwebhook.vo.twitch.eventsub.SubscriptionList;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@Component
@RequiredArgsConstructor
public class ScheduledService {
    private final OauthTokenService oauthTokenService;
    private final EventSubService eventSubService;

    @Scheduled(cron = "0 30 */1 * * *")
    public void eventSubscriptionCheck() {
        log.info("Scheduled Event Subscription Check Start");
        SubscriptionList subscriptionListListFromTwitch = eventSubService.getSubscriptionListFromTwitch();

        oauthTokenService.getRecentOauthToken();
    }
}
