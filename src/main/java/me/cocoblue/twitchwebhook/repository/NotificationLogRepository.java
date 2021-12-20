package me.cocoblue.twitchwebhook.repository;

import me.cocoblue.twitchwebhook.entity.NotificationLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLogEntity, Long> {
    NotificationLogEntity getStreamNotifyLogByIdFromTwitch(String idFromTwitch);
}
