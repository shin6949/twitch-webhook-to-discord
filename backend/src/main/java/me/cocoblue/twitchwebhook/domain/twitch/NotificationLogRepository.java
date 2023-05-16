package me.cocoblue.twitchwebhook.domain.twitch;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLogEntity, Long> {
    NotificationLogEntity getStreamNotifyLogByIdFromTwitch(String idFromTwitch);
    Page<NotificationLogEntity> getNotificationLogEntitiesByBroadcasterIdEntityOrderByReceivedTime(BroadcasterIdEntity broadcasterIdEntity, Pageable pageable);
}
