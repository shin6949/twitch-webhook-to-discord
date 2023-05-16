package me.cocoblue.twitchwebhook.domain.logdomain;

import me.cocoblue.twitchwebhook.data.TwitchSubscriptionType;
import me.cocoblue.twitchwebhook.domain.twitch.BroadcasterIdEntity;
import me.cocoblue.twitchwebhook.domain.twitch.PushSubscriptionFormEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PushUserLogViewRepository extends JpaRepository<PushUserLogViewEntity, Long> {
    int countByBroadcasterIdEntityAndTwitchSubscriptionTypeAndReceivedTimeBetween(BroadcasterIdEntity broadcasterIdEntity, TwitchSubscriptionType twitchSubscriptionType, LocalDateTime start, LocalDateTime end);
    int countByPushSubscriptionFormEntity(PushSubscriptionFormEntity pushSubscriptionFormEntity);
    Optional<PushUserLogViewEntity> findFirstByPushSubscriptionFormEntityOrderByReceivedTimeDesc(PushSubscriptionFormEntity pushSubscriptionFormEntity);
}
