package me.cocoblue.twitchwebhook.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface YouTubeSubscriptionFormRepository extends JpaRepository<YouTubeSubscriptionFormEntity, Long> {

}
