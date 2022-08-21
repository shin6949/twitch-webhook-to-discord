package me.cocoblue.twitchwebhook.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.domain.BroadcasterIdEntity;
import me.cocoblue.twitchwebhook.domain.SubscriptionFormEntity;
import me.cocoblue.twitchwebhook.dto.twitch.eventsub.Subscription;
import me.cocoblue.twitchwebhook.service.twitch.EventSubService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
@Component
@RequiredArgsConstructor
public class ScheduledService {
    private final OauthTokenService oauthTokenService;
    private final EventSubService eventSubService;
    private final NotificationFormService notificationFormService;

    @Value("${webapp.base-url}")
    private String webappBaseUrl;

    @Value("${twitch.event-renew}")
    private boolean eventEnabled;

    @Scheduled(cron = "0 30 */1 * * *")
    public void eventSubscriptionCheck() {
        if(!eventEnabled) {
            log.info("Event Renew Function Disabled. Do Not Processing.");
            return;
        }

        log.info("Event Subscription Check Start");

        final String accessToken = oauthTokenService.getAppTokenFromTwitch().getAccessToken();

        List<Subscription> subscriptionListFromTwitch;
        try {
            subscriptionListFromTwitch = eventSubService.getSubscriptionListFromTwitch(accessToken).getSubscriptionList();
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            log.warn("Not valid token error occurred.");
            return;
        } catch (Exception e) {
            log.error("This is not token error. other error is occurred.");
            e.printStackTrace();
            return;
        }

        final List<SubscriptionFormEntity> formList = notificationFormService.getFormAll();
        log.info("formList Number: " + formList.size());
        log.debug("formList Data: " + formList);

        List<SubscriptionFormEntity> requiredToEnrollEventList = new ArrayList<>();
        for(SubscriptionFormEntity form : formList) {
            for(int i = 0; i < subscriptionListFromTwitch.size(); i++) {
                if(judgeSameForm(form, subscriptionListFromTwitch.get(i))) break;

                if(i == (subscriptionListFromTwitch.size() - 1)) {
                    requiredToEnrollEventList.add(form);
                }
            }
        }

        log.info("Need To Enroll Form Number: " + requiredToEnrollEventList.size());

        for(SubscriptionFormEntity form : requiredToEnrollEventList) {
            log.debug("To Enroll Form: " + form);
            eventSubService.addEventSubToTwitch(form, accessToken);
        }

        oauthTokenService.revokeAppTokenToTwitch(accessToken);
        log.info("Scheduled Event Subscription Check Finished");
    }

    private boolean judgeSameForm(SubscriptionFormEntity form, Subscription subscription) {
        // 같은 유저를 바라보고 있는지.
        if(form.getBroadcasterIdEntity().getId() != Long.parseLong(subscription.getCondition().getBroadcasterUserId())) {
            return false;
        }

        // 같은 타입의 구독인지.
        if(!form.getSubscriptionType().getTwitchName().equals(subscription.getType())) {
            return false;
        }

        // Callback URL이 맞는지.
        return subscription.getTransport().getCallback().startsWith(webappBaseUrl);
    }
}
