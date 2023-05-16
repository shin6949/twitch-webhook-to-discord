package me.cocoblue.twitchwebhook.domain.logdomain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLogRepository extends JpaRepository<UserLogEntity, Long> {
}
