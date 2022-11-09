package me.cocoblue.twitchwebhook.domain.youtube;

import me.cocoblue.twitchwebhook.data.YouTubeSubscriptionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface YouTubeNotificationLogRepository extends JpaRepository<YouTubeNotificationLogEntity, Long> {
    List<YouTubeNotificationLogEntity> findAllByYouTubeChannelInfoEntityAndVideoId(YouTubeChannelInfoEntity youTubeChannelInfoEntity, String videoId);
//    int countByYouTubeChannelInfoEntityAndYouTubeSubscriptionTypeAndDuplicateSuspicionAndReceivedTimeBetween(YouTubeChannelInfoEntity youTubeChannelInfoEntity, YouTubeSubscriptionType youTubeSubscriptionType, boolean duplicateSuspicion, LocalDateTime start, LocalDateTime end);
}
