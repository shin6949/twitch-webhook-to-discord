package me.cocoblue.twitchwebhook.domain;

import me.cocoblue.twitchwebhook.domain.NotificationLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLogEntity, Long> {
    NotificationLogEntity getStreamNotifyLogByIdFromTwitch(String idFromTwitch);
}
