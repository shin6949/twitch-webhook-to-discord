package me.cocoblue.twitchwebhook.domain.youtube;

import me.cocoblue.twitchwebhook.data.YouTubeSubscriptionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YouTubeSubscriptionGroupViewRepository extends JpaRepository<YouTubeSubscriptionGroupViewEntity, YouTubeSubscriptionGroupViewId> {
    List<YouTubeSubscriptionGroupViewEntity> findAllByYouTubeSubscriptionType(YouTubeSubscriptionType youTubeSubscriptionType);
}
