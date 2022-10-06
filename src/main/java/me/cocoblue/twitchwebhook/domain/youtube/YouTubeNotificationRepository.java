package me.cocoblue.twitchwebhook.domain.youtube;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface YouTubeNotificationRepository extends JpaRepository<YouTubeNotificationLogEntity, Long> {
}
