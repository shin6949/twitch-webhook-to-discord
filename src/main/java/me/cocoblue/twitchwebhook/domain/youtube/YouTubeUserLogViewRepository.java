package me.cocoblue.twitchwebhook.domain.youtube;

import me.cocoblue.twitchwebhook.data.YouTubeSubscriptionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface YouTubeUserLogViewRepository extends JpaRepository<YouTubeUserLogViewEntity, Long> {
    int countByYouTubeChannelInfoEntityAndYouTubeSubscriptionTypeAndReceivedTimeBetween(YouTubeChannelInfoEntity youTubeChannelInfoEntity, YouTubeSubscriptionType youTubeSubscriptionType, LocalDateTime start, LocalDateTime end);
}
