package me.cocoblue.twitchwebhook.domain;

import me.cocoblue.twitchwebhook.data.SubscriptionType;
import me.cocoblue.twitchwebhook.domain.BroadcasterIdEntity;
import me.cocoblue.twitchwebhook.domain.SubscriptionFormEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionFormRepository extends JpaRepository<SubscriptionFormEntity, Long> {
    List<SubscriptionFormEntity> getStreamNotifyFormsByBroadcasterIdEntityAndSubscriptionType(BroadcasterIdEntity broadcasterIdEntity, SubscriptionType type);
}
