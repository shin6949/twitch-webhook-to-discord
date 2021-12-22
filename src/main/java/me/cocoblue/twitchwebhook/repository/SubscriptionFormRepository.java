package me.cocoblue.twitchwebhook.repository;

import me.cocoblue.twitchwebhook.dto.SubscriptionType;
import me.cocoblue.twitchwebhook.entity.BroadcasterIdEntity;
import me.cocoblue.twitchwebhook.entity.SubscriptionFormEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionFormRepository extends JpaRepository<SubscriptionFormEntity, Long> {
    List<SubscriptionFormEntity> getStreamNotifyFormsByBroadcasterIdEntityAndSubscriptionType(BroadcasterIdEntity broadcasterIdEntity, SubscriptionType type);
}
