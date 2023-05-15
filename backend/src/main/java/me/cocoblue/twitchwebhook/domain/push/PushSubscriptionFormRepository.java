package me.cocoblue.twitchwebhook.domain.push;

import me.cocoblue.twitchwebhook.data.TwitchSubscriptionType;
import me.cocoblue.twitchwebhook.domain.twitch.BroadcasterIdEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PushSubscriptionFormRepository extends JpaRepository<PushSubscriptionFormEntity, Long> {
    List<PushSubscriptionFormEntity> getPushSubscriptionFormEntitiesByBroadcasterIdEntityAndTwitchSubscriptionType(BroadcasterIdEntity broadcasterIdEntity, TwitchSubscriptionType twitchSubscriptionType);
    @Transactional
    @Modifying
    @Query(value = "UPDATE push_subscription_form " +
            "SET enabled = true " +
            "WHERE broadcaster_id = :broadcaster_id " +
            "AND type = :type", nativeQuery = true)
    int updateEnabled(@Param(value = "broadcaster_id") long broadcasterId, @Param(value = "type") String type);
}
