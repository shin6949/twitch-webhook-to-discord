package me.cocoblue.twitchwebhook.domain.twitch;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PushUUIDStorageRepository extends JpaRepository<PushUUIDStorageEntity, String> {
}
