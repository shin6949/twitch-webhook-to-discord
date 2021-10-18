package me.cocoblue.twitchwebhook.repository;

import me.cocoblue.twitchwebhook.entity.StreamNotifyLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StreamNotifyLogRepository extends JpaRepository<StreamNotifyLog, Long> {
    StreamNotifyLog getStreamNotifyLogByIdFromTwitch(String idFromTwitch);
}
