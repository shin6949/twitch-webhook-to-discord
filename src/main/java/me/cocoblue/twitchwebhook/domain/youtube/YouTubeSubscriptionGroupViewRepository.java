package me.cocoblue.twitchwebhook.domain.youtube;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YouTubeSubscriptionGroupViewRepository extends JpaRepository<YouTubeSubscriptionGroupViewEntity, YouTubeSubscriptionGroupViewId> {
    List<YouTubeSubscriptionGroupViewEntity> findAllByEnabled(boolean enabled);
}
