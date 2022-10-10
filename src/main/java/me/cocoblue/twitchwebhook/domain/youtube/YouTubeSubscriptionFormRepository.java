package me.cocoblue.twitchwebhook.domain.youtube;

import me.cocoblue.twitchwebhook.data.YouTubeSubscriptionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YouTubeSubscriptionFormRepository extends JpaRepository<YouTubeSubscriptionFormEntity, Long> {
    List<YouTubeSubscriptionFormEntity> findAllByEnabled(boolean enabled);
    List<YouTubeSubscriptionFormEntity> findAllByChannelIdAndYouTubeSubscriptionType(String channelId, YouTubeSubscriptionType youTubeSubscriptionType);

}
