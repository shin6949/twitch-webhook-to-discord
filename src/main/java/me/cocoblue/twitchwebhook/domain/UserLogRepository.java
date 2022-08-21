package me.cocoblue.twitchwebhook.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLogRepository extends JpaRepository<Long, UserLogEntity> {
}
