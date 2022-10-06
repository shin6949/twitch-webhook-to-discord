package me.cocoblue.twitchwebhook.domain.youtube;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YouTubeSubscriptionFormRepository extends JpaRepository<YouTubeSubscriptionFormEntity, Long> {
    List<YouTubeSubscriptionFormEntity> findAllByEnabled(boolean enabled);
}
