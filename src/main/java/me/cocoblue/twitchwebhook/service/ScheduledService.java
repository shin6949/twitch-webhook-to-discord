package me.cocoblue.twitchwebhook.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.dto.Form;
import me.cocoblue.twitchwebhook.service.twitch.EventSubService;
import me.cocoblue.twitchwebhook.vo.twitch.eventsub.Subscription;
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
    private final FormService formService;

    @Scheduled(cron = "0 30 */1 * * *")
    public void eventSubscriptionCheck() {
        log.info("Event Subscription Check Start");

        final List<Subscription> subscriptionListFromTwitch = eventSubService.getSubscriptionListFromTwitch().getSubscriptionList();
        final List<Form> formList = formService.getFormAll();
        log.info("formList Number: " + formList.size());

        List<Form> requiredToEnrollEventList = new ArrayList<>();
        for(Form form : formList) {
            for(int i = 0; i < subscriptionListFromTwitch.size(); i++) {
                if(form.getBroadcasterId() == Integer.parseInt(subscriptionListFromTwitch.get(i).getCondition().getBroadcasterUserId())
                && form.getType().equals(subscriptionListFromTwitch.get(i).getType())) {
                    break;
                }

                if(i == (subscriptionListFromTwitch.size() - 1)) {
                    requiredToEnrollEventList.add(form);
                }
            }
        }

        log.info("Need To Enroll Form Number: " + requiredToEnrollEventList.size());

        for(Form form : requiredToEnrollEventList) {
            log.info("To Enroll Form: " + form);
            eventSubService.addEventSubToTwitch(form);
        }

        log.info("Scheduled Event Subscription Check Finished");
    }
}
