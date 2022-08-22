package me.cocoblue.twitchwebhook.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.domain.BroadcasterIdEntity;
import me.cocoblue.twitchwebhook.domain.SubscriptionFormEntity;
import me.cocoblue.twitchwebhook.domain.SubscriptionFormRepository;
import me.cocoblue.twitchwebhook.dto.twitch.eventsub.Subscription;
import me.cocoblue.twitchwebhook.service.twitch.EventSubService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
@Component
@RequiredArgsConstructor
public class ScheduledService {
    private final OauthTokenService oauthTokenService;
    private final EventSubService eventSubService;
    private final SubscriptionFormRepository subscriptionFormRepository;

    @Value("${webapp.base-url}")
    private String webappBaseUrl;

    @Value("${twitch.event-renew}")
    private boolean eventEnabled;

    @Scheduled(cron = "0 30 */1 * * *")
    public void eventSubscriptionCheck() {
        /*
            TODO: 스케쥴 연산을 줄이기 위한 구현 전략
            - 동일 ID + 동일 Type으로 GROUP BY하여 ROW를 최소화하여 받아오도록.
            -> 주기적으로 확인해야하므로 DB에서 View으로 정의할 필요가 있음.
         */
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

        final List<SubscriptionFormEntity> formList = subscriptionFormRepository.findAll();
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

    @Scheduled(cron = "0 */5 * * * *")
    public void eventSubscriptionAdd() {
        if(!eventEnabled) {
            log.info("Event Renew Function Disabled. Do Not Processing.");
            return;
        }

        log.info("Event Subscription Add Start");
        log.info("Getting Not Enabled Form");

        final LocalDateTime nowTime = LocalDateTime.now();
        /*
            TODO: 스케쥴 연산을 줄이기 위한 구현 전략
             1. 미 구독된 webhook 요청의 경우에는 동일 ID + 동일 Type으로 GROUP BY하여 ROW를 최소화하여 받아오도록.
             -> 주기적으로 확인해야하므로 DB에서 View으로 정의할 필요가 있음.
             2. FE 단에서 데이터를 추가했을 때 이미 추가되어 있는 구독인지 확인한다. 이미 추가되어 있다면 enabled를 처음부터 True로 설정.
         */
        final List<SubscriptionFormEntity> toAddSubscriptionForms = subscriptionFormRepository
                .getSubscriptionFormEntitiesByEnabledFalseAndCreatedAtBetween(nowTime.minusMinutes(6), nowTime);

        // 중복 구독이 있다면 TRUE로 변경, 없다면 등록 진행
        for(SubscriptionFormEntity subscriptionFormEntity : toAddSubscriptionForms) {

        }
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
