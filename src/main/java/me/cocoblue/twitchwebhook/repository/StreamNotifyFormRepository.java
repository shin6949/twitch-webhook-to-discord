package me.cocoblue.twitchwebhook.repository;

import me.cocoblue.twitchwebhook.entity.BroadcasterIdEntity;
import me.cocoblue.twitchwebhook.entity.StreamNotifyFormEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StreamNotifyFormRepository extends JpaRepository<StreamNotifyFormEntity, Long> {
    List<StreamNotifyFormEntity> getStreamNotifyFormsByBroadcasterIdAndType(BroadcasterIdEntity broadcasterIdEntity, String type);
}
