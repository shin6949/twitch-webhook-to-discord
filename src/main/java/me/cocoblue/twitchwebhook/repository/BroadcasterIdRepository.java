package me.cocoblue.twitchwebhook.repository;

import me.cocoblue.twitchwebhook.entity.BroadcasterIdEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BroadcasterIdRepository extends JpaRepository<BroadcasterIdEntity, Long> {

}
