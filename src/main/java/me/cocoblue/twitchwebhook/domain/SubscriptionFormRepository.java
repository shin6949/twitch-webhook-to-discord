package me.cocoblue.twitchwebhook.domain;

import me.cocoblue.twitchwebhook.data.SubscriptionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SubscriptionFormRepository extends JpaRepository<SubscriptionFormEntity, Long> {
    List<SubscriptionFormEntity> getStreamNotifyFormsByBroadcasterIdEntityAndSubscriptionType(BroadcasterIdEntity broadcasterIdEntity, SubscriptionType type);
    List<SubscriptionFormEntity> getSubscriptionFormEntitiesByEnabledFalseAndCreatedAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
    List<SubscriptionFormEntity> getSubscriptionFormEntitiesByEnabledTrueAndBroadcasterIdEntityAndSubscriptionType(BroadcasterIdEntity broadcasterIdEntity, SubscriptionType type);
    @Query("UPDATE subscription_form SET enabled = true " +
            "WHERE broadcaster_id = :id " +
            "AND type = :type")
    @Modifying
    void updateEnabledTrueByBroadcasterIdAndType(@Param("id") long broadcasterId, @Param("type") String Type);
}
