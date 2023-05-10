package me.cocoblue.twitchwebhook.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BroadcasterIdRepository extends JpaRepository<BroadcasterIdEntity, Long> {
    Optional<BroadcasterIdEntity> getBroadcasterIdEntityByIdEquals(long id);
    Optional<BroadcasterIdEntity> getBroadcasterIdEntityByLoginIdEquals(String loginId);
}
