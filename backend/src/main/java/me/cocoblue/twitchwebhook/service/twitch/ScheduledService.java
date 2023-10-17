package me.cocoblue.twitchwebhook.service.twitch;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.domain.discord.SubscriptionFormRepository;
import me.cocoblue.twitchwebhook.domain.twitch.PushSubscriptionFormRepository;
import me.cocoblue.twitchwebhook.domain.twitch.SubscriptionGroupViewEntity;
import me.cocoblue.twitchwebhook.domain.twitch.SubscriptionGroupViewRepository;
import me.cocoblue.twitchwebhook.dto.twitch.eventsub.Subscription;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
@Component
@RequiredArgsConstructor
public class ScheduledService {
    private final OauthTokenService oauthTokenService;
    private final EventSubService eventSubService;
    private final SubscriptionGroupViewRepository subscriptionGroupViewRepository;
    private final PushSubscriptionFormRepository pushSubscriptionFormRepository;
    private final SubscriptionFormRepository subscriptionFormRepository;

    @Value("${webapp.base-url}")
    private String webappBaseUrl;

    @Value("${twitch.event-renew}")
    private boolean eventEnabled;

    @Scheduled(cron = "0 30 */1 * * *")
    public void eventSubscriptionCheck() {
        if (!eventEnabled) {
            log.info("Event Renew Function Disabled. Do Not Processing.");
            return;
        }

        log.info("Event Subscription Check Start");

        final List<Subscription> subscriptionListFromTwitch = getSubscriptionsFromTwitch();
        if (subscriptionListFromTwitch == null) return;

        final List<SubscriptionGroupViewEntity> formList = subscriptionGroupViewRepository.findAll();

        final List<SubscriptionGroupViewEntity> requiredToEnrollEventList = formList.stream()
                .filter(form -> subscriptionListFromTwitch.stream().noneMatch(subscription -> isSameSubscription(form, subscription)))
                .collect(Collectors.toList());

        updateAndEnrollForms(requiredToEnrollEventList);

        log.info("Scheduled Event Subscription Check Finished");
    }

    @Scheduled(cron = "0 */5 * * * *")
    public void eventSubscriptionAdd() {
        if (!eventEnabled) {
            log.info("Event Renew Function Disabled. Do Not Processing.");
            return;
        }

        log.info("Event Subscription Add Start");

        final List<SubscriptionGroupViewEntity> toAddSubscriptionForms = subscriptionGroupViewRepository.findAllByEnabled(false);
        updateAndEnrollForms(toAddSubscriptionForms);

        log.info("Event Subscription Add Finished");
    }

    private List<Subscription> getSubscriptionsFromTwitch() {
        final String accessToken = oauthTokenService.getAppTokenFromTwitch().getAccessToken();
        try {
            return eventSubService.getSubscriptionListFromTwitch(accessToken).getSubscriptionList();
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            log.warn("Not valid token error occurred.");
        } catch (Exception e) {
            log.error("This is not token error. other error is occurred.");
            e.printStackTrace();
        }
        return null;
    }

    private boolean isSameSubscription(final SubscriptionGroupViewEntity form, final Subscription subscription) {
        return form.getBroadcasterId() == Long.parseLong(subscription.getCondition().getBroadcasterUserId())
                && form.getSubscriptionType().getTwitchName().equals(subscription.getType())
                && subscription.getTransport().getCallback().startsWith(webappBaseUrl + "/webhook/twitch");
    }

    private void updateAndEnrollForms(final List<SubscriptionGroupViewEntity> forms) {
        final String accessToken = oauthTokenService.getAppTokenFromTwitch().getAccessToken();
        forms.forEach(form -> {
            if (subscriptionGroupViewRepository.countSubscriptionGroupViewEntitiesBySubscriptionGroupViewIdAndEnabled(
                    form.getSubscriptionGroupViewId(), true) > 0) {
                updateEnabledTrue(form);
            } else if (eventSubService.addEventSubToTwitch(form, accessToken)) {
                updateEnabledTrue(form);
            }
        });
        oauthTokenService.revokeAppTokenToTwitch(accessToken);
    }

    private void updateEnabledTrue(final SubscriptionGroupViewEntity subscriptionGroupViewEntity) {
        int result = subscriptionFormRepository.updateEnabled(
                subscriptionGroupViewEntity.getBroadcasterId(),
                subscriptionGroupViewEntity.getSubscriptionType().toString());
        log.info("Updated subscription_form Table {} Rows. To enabled=true", result);

        result = pushSubscriptionFormRepository.updateEnabled(
                subscriptionGroupViewEntity.getBroadcasterId(),
                subscriptionGroupViewEntity.getSubscriptionType().toString());
        log.info("Updated push_subscription_form Table {} Rows. To enabled=true", result);
    }
}
