package me.cocoblue.twitchwebhook.service.twitch;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.data.TwitchSubscriptionType;
import me.cocoblue.twitchwebhook.domain.discord.SubscriptionFormEntity;
import me.cocoblue.twitchwebhook.domain.discord.UserLogEntity;
import me.cocoblue.twitchwebhook.domain.discord.UserLogRepository;
import me.cocoblue.twitchwebhook.domain.discord.UserLogViewRepository;
import me.cocoblue.twitchwebhook.domain.push.PushSubscriptionFormEntity;
import me.cocoblue.twitchwebhook.domain.push.PushUserLogEntity;
import me.cocoblue.twitchwebhook.domain.push.PushUserLogRepository;
import me.cocoblue.twitchwebhook.domain.twitch.BroadcasterIdEntity;
import me.cocoblue.twitchwebhook.domain.twitch.BroadcasterIdRepository;
import me.cocoblue.twitchwebhook.domain.twitch.NotificationLogEntity;
import org.springframework.stereotype.Service;

import javax.management.Notification;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
public class TwitchUserLogService {
    private final UserLogRepository userLogRepository;
    private final BroadcasterIdRepository broadcasterIdRepository;
    private final UserLogViewRepository userLogViewRepository;
    private final PushUserLogRepository pushUserLogRepository;

    public void insertUserLog(final SubscriptionFormEntity notifyForm, final NotificationLogEntity notificationLogEntity, final boolean status) {
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

    public void insertPushUserLog(final long pushSubscriptionFormId,
                                  final long notificationLogId, final boolean status) {
        final PushSubscriptionFormEntity pushSubscriptionFormEntity = PushSubscriptionFormEntity.builder()
                .id(pushSubscriptionFormId)
                .build();

        final NotificationLogEntity notificationLogEntity = NotificationLogEntity.builder()
                .id(notificationLogId)
                .build();

        final PushUserLogEntity pushUserLogEntity = PushUserLogEntity.builder()
                .logId(notificationLogEntity)
                .pushSubscriptionFormEntity(pushSubscriptionFormEntity)
                .status(status)
                .build();

        pushUserLogRepository.save(pushUserLogEntity);
        if(pushUserLogEntity.getId() != 0) {
            log.info("Push User Log Insert Finished");
        } else {
            log.info("Push User Log Insert Failed");
        }
    }

    public boolean isNotInInterval(final String broadcasterId, final TwitchSubscriptionType twitchSubscriptionType, int intervalMinute) {
        // interval이 0일 경우 알림을 계속 받겠다는 의미
        if(intervalMinute == 0) {
            return true;
        }

        final LocalDateTime nowTime = LocalDateTime.now();
        final Optional<BroadcasterIdEntity> broadcasterIdEntity = broadcasterIdRepository.getBroadcasterIdEntityByIdEquals(Long.parseLong(broadcasterId));
        if(broadcasterIdEntity.isEmpty()) {
            // 미등록된 유저이므로 중복 알림은 아님.
            return true;
        }

        final int notificationCount = userLogViewRepository.countByBroadcasterIdEntityAndTwitchSubscriptionTypeAndReceivedTimeBetween
                (broadcasterIdEntity.get(), twitchSubscriptionType, nowTime.minusMinutes(intervalMinute), nowTime);

        log.info("Notification Count: " + notificationCount);
        return notificationCount <= 0;
    }
}
