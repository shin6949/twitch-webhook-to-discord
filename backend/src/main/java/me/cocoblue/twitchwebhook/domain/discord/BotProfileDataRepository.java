package me.cocoblue.twitchwebhook.domain.discord;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BotProfileDataRepository extends JpaRepository<BotProfileDataEntity, Long> {
}
