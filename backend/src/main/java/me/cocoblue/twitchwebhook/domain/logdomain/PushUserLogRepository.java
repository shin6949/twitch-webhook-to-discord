package me.cocoblue.twitchwebhook.domain.logdomain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PushUserLogRepository extends JpaRepository<PushUserLogEntity, Long> {
}
