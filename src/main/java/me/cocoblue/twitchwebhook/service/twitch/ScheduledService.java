package me.cocoblue.twitchwebhook.service.twitch;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.domain.SubscriptionFormRepository;
import me.cocoblue.twitchwebhook.domain.SubscriptionGroupViewEntity;
import me.cocoblue.twitchwebhook.domain.SubscriptionGroupViewRepository;
import me.cocoblue.twitchwebhook.dto.twitch.eventsub.Subscription;
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
    private final SubscriptionGroupViewRepository subscriptionGroupViewRepository;
    private final SubscriptionFormRepository subscriptionFormRepository;

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

        final List<SubscriptionGroupViewEntity> formList = subscriptionGroupViewRepository.findAll();
        log.info("formList Number: " + formList.size());
        log.debug("formList Data: " + formList);

        List<SubscriptionGroupViewEntity> requiredToEnrollEventList = new ArrayList<>();
        for(SubscriptionGroupViewEntity form : formList) {
            for(int i = 0; i < subscriptionListFromTwitch.size(); i++) {
                if(judgeSameForm(form, subscriptionListFromTwitch.get(i))) {
                    // 활성화가 안 된 부분 처리
                    updateEnabledTrue(form);
                    break;
                }

                if(i == (subscriptionListFromTwitch.size() - 1)) {
                    requiredToEnrollEventList.add(form);
                }
            }
        }

        log.info("Need To Enroll Form Number: " + requiredToEnrollEventList.size());

        for(SubscriptionGroupViewEntity form : requiredToEnrollEventList) {
            log.debug("To Enroll Form: " + form);
            if(eventSubService.addEventSubToTwitch(form, accessToken)) {
                updateEnabledTrue(form);
            }
        }

        oauthTokenService.revokeAppTokenToTwitch(accessToken);
        log.info("Scheduled Event Subscription Check Finished");
    }

    @Scheduled(cron = "0 */5 * * * *")
    public void eventSubscriptionAdd() {
        if(!eventEnabled) {
            log.info("Event Renew Function Disabled. Do Not Processing.");
            return;
        }

        log.info("Event Subscription Add Start");
        log.info("Getting Not Enabled Form");

        final List<SubscriptionGroupViewEntity> toAddSubscriptionForms = subscriptionGroupViewRepository.findAllByEnabled(false);
        if(toAddSubscriptionForms.size() == 0) {
            log.info("toAddSubscriptionForms's Size is 0. Job Finished");
            return;
        }

        final String accessToken = oauthTokenService.getAppTokenFromTwitch().getAccessToken();

        for(SubscriptionGroupViewEntity subscriptionGroupViewEntity : toAddSubscriptionForms) {
            // 이미 활성화된 다른 항목이 있다면 바로 true로 변경
            int count = subscriptionGroupViewRepository.countSubscriptionGroupViewEntitiesBySubscriptionGroupViewIdAndEnabled(
                    subscriptionGroupViewEntity.getSubscriptionGroupViewId(), true);
            if(count > 0) {
                updateEnabledTrue(subscriptionGroupViewEntity);
                continue;
            }

            if(eventSubService.addEventSubToTwitch(subscriptionGroupViewEntity, accessToken)) {
                updateEnabledTrue(subscriptionGroupViewEntity);
            }
        }

        oauthTokenService.revokeAppTokenToTwitch(accessToken);
        log.info("Event Subscription Add Finished");
    }

    private boolean judgeSameForm(SubscriptionGroupViewEntity form, Subscription subscription) {
        // 같은 유저를 바라보고 있는지.
        if(form.getBroadcasterId() != Long.parseLong(subscription.getCondition().getBroadcasterUserId())) {
            return false;
        }

        // 같은 타입의 구독인지.
        if(!form.getSubscriptionType().getTwitchName().equals(subscription.getType())) {
            return false;
        }

        // Callback URL이 맞는지.
        return subscription.getTransport().getCallback().startsWith(webappBaseUrl + "/webhook/twitch");
    }

    private void updateEnabledTrue(SubscriptionGroupViewEntity subscriptionGroupViewEntity) {
        log.debug("To Modifying BroadcasterId: " + subscriptionGroupViewEntity.getBroadcasterId());
        log.debug("To string: " + subscriptionGroupViewEntity.getSubscriptionType().toString());
        try {
            final int result = subscriptionFormRepository.updateEnabled(
                    subscriptionGroupViewEntity.getBroadcasterId(),
                    subscriptionGroupViewEntity.getSubscriptionType().toString());
            log.info("Updated " + result + " Rows. To enabled=true");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
