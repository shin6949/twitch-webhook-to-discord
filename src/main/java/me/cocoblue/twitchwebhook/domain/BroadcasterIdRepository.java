package me.cocoblue.twitchwebhook.domain;

import me.cocoblue.twitchwebhook.domain.BroadcasterIdEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BroadcasterIdRepository extends JpaRepository<BroadcasterIdEntity, Long> {
    BroadcasterIdEntity getBroadcasterIdEntityByIdEquals(long id);
}
