package me.cocoblue.twitchwebhook.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.entity.SubscriptionFormEntity;
import me.cocoblue.twitchwebhook.service.twitch.EventSubService;
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
    private final EventSubService eventSubService;
    private final NotificationFormService notificationFormService;

    @Value("${webapp.base.url}")
    private String webappBaseUrl;

    @Scheduled(cron = "0 30 */1 * * *")
    public void eventSubscriptionCheck() {
        log.info("Event Subscription Check Start");

        final List<Subscription> subscriptionListFromTwitch = eventSubService.getSubscriptionListFromTwitch().getSubscriptionList();
        final List<SubscriptionFormEntity> formList = notificationFormService.getFormAll();
        log.info("formList Number: " + formList.size());

        List<SubscriptionFormEntity> requiredToEnrollEventList = new ArrayList<>();
        for(SubscriptionFormEntity form : formList) {
            for(int i = 0; i < subscriptionListFromTwitch.size(); i++) {
                if(form.getBroadcasterIdEntity().getId() == Long.parseLong(subscriptionListFromTwitch.get(i).getCondition().getBroadcasterUserId())
                && form.getSubscriptionType().getTwitchName().equals(subscriptionListFromTwitch.get(i).getType())
                && subscriptionListFromTwitch.get(i).getTransport().getCallback().startsWith(webappBaseUrl)) {
                    break;
                }

                if(i == (subscriptionListFromTwitch.size() - 1)) {
                    requiredToEnrollEventList.add(form);
                }
            }
        }

        log.info("Need To Enroll Form Number: " + requiredToEnrollEventList.size());

        for(SubscriptionFormEntity form : requiredToEnrollEventList) {
            log.info("To Enroll Form: " + form);
            eventSubService.addEventSubToTwitch(form);
        }

        log.info("Scheduled Event Subscription Check Finished");
    }
}
