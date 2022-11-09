package me.cocoblue.twitchwebhook.domain;

import me.cocoblue.twitchwebhook.data.TwitchSubscriptionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLogEntity, Long> {
    NotificationLogEntity getStreamNotifyLogByIdFromTwitch(String idFromTwitch);
    Page<NotificationLogEntity> getNotificationLogEntitiesByBroadcasterIdEntityOrderByReceivedTime(BroadcasterIdEntity broadcasterIdEntity, Pageable pageable);
    int countByBroadcasterIdEntityAndTwitchSubscriptionTypeAndIsDuplicateSuspicionAndReceivedTimeBetween(BroadcasterIdEntity broadcasterIdEntity, TwitchSubscriptionType twitchSubscriptionType,  boolean duplicateSuspicion, LocalDateTime start, LocalDateTime end);
}
