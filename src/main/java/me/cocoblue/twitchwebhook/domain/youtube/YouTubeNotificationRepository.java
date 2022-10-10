package me.cocoblue.twitchwebhook.domain.youtube;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YouTubeNotificationRepository extends JpaRepository<YouTubeNotificationLogEntity, Long> {
    List<YouTubeNotificationLogEntity> findAllByChannelIdAndVideoId(String channelId, String videoId);
}
