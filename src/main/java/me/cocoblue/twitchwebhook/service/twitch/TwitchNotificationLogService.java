package me.cocoblue.twitchwebhook.service.twitch;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.data.TwitchSubscriptionType;
import me.cocoblue.twitchwebhook.domain.BroadcasterIdEntity;
import me.cocoblue.twitchwebhook.domain.BroadcasterIdRepository;
import me.cocoblue.twitchwebhook.dto.CommonEvent;
import me.cocoblue.twitchwebhook.domain.NotificationLogEntity;
import me.cocoblue.twitchwebhook.domain.NotificationLogRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Log4j2
@AllArgsConstructor
public class TwitchNotificationLogService {
    private final NotificationLogRepository notificationLogRepository;
    private final BroadcasterIdRepository broadcasterIdRepository;

    public Boolean isAlreadySend(String idFromTwitch) {
        return getLogByIdFromTwitch(idFromTwitch) != null;
    }

    @Transactional
    public NotificationLogEntity insertLog(CommonEvent event, HttpHeaders headers, boolean isDuplicateSuspicion) {
        log.debug("event: " + event);
        final String messageId = headers.get("twitch-eventsub-message-id").get(0);
        event.setNotificationIdFromTwitch(messageId);

        return notificationLogRepository.save(event.toNotificationLogEntity(isDuplicateSuspicion));
    }

    private NotificationLogEntity getLogByIdFromTwitch(String idFromTwitch) {
        return notificationLogRepository.getStreamNotifyLogByIdFromTwitch(idFromTwitch);
    }

    public boolean isDuplicateSuspicionNotification(String broadcasterId, TwitchSubscriptionType twitchSubscriptionType) {
        final LocalDateTime nowTime = LocalDateTime.now();
        final Optional<BroadcasterIdEntity> broadcasterIdEntity = broadcasterIdRepository.getBroadcasterIdEntityByIdEquals(Long.parseLong(broadcasterId));
        if(broadcasterIdEntity.isEmpty()) {
            // 미등록된 유저이므로 중복 알림은 아님.
            return false;
        }

        final int notificationCount = notificationLogRepository.countByBroadcasterIdEntityAndTwitchSubscriptionTypeAndIsDuplicateSuspicionAndReceivedTimeBetween
                (broadcasterIdEntity.get(), twitchSubscriptionType, false, nowTime.minusMinutes(10), nowTime);

        log.info("Notification Count: " + notificationCount);
        return notificationCount > 0;
    }
}
