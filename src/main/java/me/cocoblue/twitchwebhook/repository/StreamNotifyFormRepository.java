package me.cocoblue.twitchwebhook.repository;

import me.cocoblue.twitchwebhook.entity.BroadcasterId;
import me.cocoblue.twitchwebhook.entity.StreamNotifyForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StreamNotifyFormRepository extends JpaRepository<StreamNotifyForm, Long> {
    List<StreamNotifyForm> getStreamNotifyFormsByBroadcasterIdAndType(BroadcasterId broadcasterId, String type);
}
