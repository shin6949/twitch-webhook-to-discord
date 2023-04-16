package me.cocoblue.twitchwebhook.domain;

import me.cocoblue.twitchwebhook.data.TwitchSubscriptionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface UserLogViewRepository extends JpaRepository<UserLogViewEntity, Long> {
    int countByBroadcasterIdEntityAndTwitchSubscriptionTypeAndReceivedTimeBetween(BroadcasterIdEntity broadcasterIdEntity, TwitchSubscriptionType twitchSubscriptionType, LocalDateTime start, LocalDateTime end);
}
