package me.cocoblue.twitchwebhook.service.twitch;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.data.TwitchSubscriptionType;
import me.cocoblue.twitchwebhook.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
public class TwitchUserLogService {
    private final UserLogRepository userLogRepository;
    private final BroadcasterIdRepository broadcasterIdRepository;
    private final UserLogViewRepository userLogViewRepository;

    public void insertUserLog(SubscriptionFormEntity notifyForm, NotificationLogEntity notificationLogEntity, boolean status) {
        final UserLogEntity userLogEntity = UserLogEntity.builder()
                .logId(notificationLogEntity)
                .subscriptionFormEntity(notifyForm)
                .status(status)
                .build();

        userLogRepository.save(userLogEntity);
        if(userLogEntity.getId() != 0) {
            log.info("User Log Insert Finished");
        } else {
            log.info("User Log Insert Failed");
        }
    }

    public boolean isTakeInterval(String broadcasterId, TwitchSubscriptionType twitchSubscriptionType, int intervalMinute) {
        // interval이 0일 경우 알림을 계속 받겠다는 의미
        if(intervalMinute == 0) {
            return false;
        }

        final LocalDateTime nowTime = LocalDateTime.now();
        final Optional<BroadcasterIdEntity> broadcasterIdEntity = broadcasterIdRepository.getBroadcasterIdEntityByIdEquals(Long.parseLong(broadcasterId));
        if(broadcasterIdEntity.isEmpty()) {
            // 미등록된 유저이므로 중복 알림은 아님.
            return false;
        }

        final int notificationCount = userLogViewRepository.countByBroadcasterIdEntityAndTwitchSubscriptionTypeAndReceivedTimeBetween
                (broadcasterIdEntity.get(), twitchSubscriptionType, nowTime.minusMinutes(intervalMinute), nowTime);

        log.info("Notification Count: " + notificationCount);
        return notificationCount > 0;
    }
}
