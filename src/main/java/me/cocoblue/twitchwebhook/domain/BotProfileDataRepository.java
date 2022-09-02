package me.cocoblue.twitchwebhook.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BotProfileDataRepository extends JpaRepository<BotProfileDataEntity, Long> {
}
