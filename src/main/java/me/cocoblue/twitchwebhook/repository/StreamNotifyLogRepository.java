package me.cocoblue.twitchwebhook.repository;

import me.cocoblue.twitchwebhook.entity.StreamNotifyLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StreamNotifyLogRepository extends JpaRepository<StreamNotifyLogEntity, Long> {
    StreamNotifyLogEntity getStreamNotifyLogByIdFromTwitch(String idFromTwitch);
}
