package me.cocoblue.twitchwebhook.domain.youtube;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YouTubeNotificationLogRepository extends JpaRepository<YouTubeNotificationLogEntity, Long> {
    List<YouTubeNotificationLogEntity> findAllByYouTubeChannelInfoEntityAndVideoId(YouTubeChannelInfoEntity youTubeChannelInfoEntity, String videoId);
}
