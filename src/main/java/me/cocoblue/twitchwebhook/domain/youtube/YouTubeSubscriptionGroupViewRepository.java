package me.cocoblue.twitchwebhook.domain.youtube;

import me.cocoblue.twitchwebhook.data.YouTubeSubscriptionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YouTubeSubscriptionGroupViewRepository extends JpaRepository<YouTubeSubscriptionGroupViewEntity, YouTubeSubscriptionGroupViewId> {
    List<YouTubeSubscriptionGroupViewEntity> findAllByYouTubeSubscriptionType(YouTubeSubscriptionType youTubeSubscriptionType);
    @Query(value = "SELECT * FROM youtube_subscription_group_view GROUP BY youtube_channel_id", nativeQuery = true)
    List<YouTubeSubscriptionGroupViewEntity> findAllChannelId();

    @Query(value = "SELECT * FROM youtube_subscription_group_view WHERE enabled = :enabled GROUP BY youtube_channel_id", nativeQuery = true)
    List<YouTubeSubscriptionGroupViewEntity> findAllByEnabledGroupByChannelId(@Param("enabled") boolean enabled);
}
