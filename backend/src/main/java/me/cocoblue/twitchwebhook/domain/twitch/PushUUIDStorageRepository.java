package me.cocoblue.twitchwebhook.domain.twitch;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface PushUUIDStorageRepository extends JpaRepository<PushUUIDStorageEntity, String> {
    Optional<PushUUIDStorageEntity> getPushUUIDStorageEntityByFcmToken(String fcmToken);
    Optional<PushUUIDStorageEntity> getPushUUIDStorageEntityByUuid(String uuid);
}
