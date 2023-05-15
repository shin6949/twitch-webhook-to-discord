package me.cocoblue.twitchwebhook.domain.push;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PushUserLogRepository extends JpaRepository<PushUserLogEntity, Long> {
}
