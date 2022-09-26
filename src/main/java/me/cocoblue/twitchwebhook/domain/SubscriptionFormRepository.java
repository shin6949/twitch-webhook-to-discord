package me.cocoblue.twitchwebhook.domain;

import me.cocoblue.twitchwebhook.data.SubscriptionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionFormRepository extends JpaRepository<SubscriptionFormEntity, Long> {
    List<SubscriptionFormEntity> getStreamNotifyFormsByBroadcasterIdEntityAndSubscriptionType(BroadcasterIdEntity broadcasterIdEntity, SubscriptionType type);
    @Transactional
    @Modifying
    @Query(value = "UPDATE subscription_form " +
            "SET enabled = true " +
            "WHERE broadcaster_id = :broadcaster_id " +
            "AND type = :type", nativeQuery = true)
    int updateEnabled(@Param(value = "broadcaster_id") long broadcasterId, @Param(value = "type") String type);
    List<SubscriptionFormEntity> getSubscriptionFormEntityByBroadcasterIdEntity(BroadcasterIdEntity broadcasterIdEntity);
}
