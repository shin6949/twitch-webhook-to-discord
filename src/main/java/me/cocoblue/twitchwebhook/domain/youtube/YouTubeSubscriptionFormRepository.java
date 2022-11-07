package me.cocoblue.twitchwebhook.domain.youtube;

import me.cocoblue.twitchwebhook.data.YouTubeSubscriptionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface YouTubeSubscriptionFormRepository extends JpaRepository<YouTubeSubscriptionFormEntity, Long> {
    List<YouTubeSubscriptionFormEntity> findAllByYouTubeChannelInfoEntityAndYouTubeSubscriptionType(YouTubeChannelInfoEntity youTubeChannelInfoEntity, YouTubeSubscriptionType youTubeSubscriptionType);

    @Transactional
    @Modifying
    @Query(value = "UPDATE youtube_subscription_form " +
            "SET enabled = true " +
            "WHERE channel_id = " +
            "   (SELECT internal_id " +
            "   FROM youtube_channel_info " +
            "   WHERE youtube_channel_id = :youtubeChannelId " +
            "   AND type = :type)", nativeQuery = true)
    int updateEnabled(@Param(value = "youtubeChannelId") String youtubeChannelId, @Param(value = "type") String type);
}
