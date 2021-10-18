package me.cocoblue.twitchwebhook.repository;

import me.cocoblue.twitchwebhook.entity.OauthTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OauthTokenRepository extends JpaRepository<OauthTokenEntity, Long> {
    OauthTokenEntity findFirstByOrderByCreateDateDesc();
}
