package me.cocoblue.twitchwebhook.domain.discord;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLogRepository extends JpaRepository<UserLogEntity, Long> {
}
