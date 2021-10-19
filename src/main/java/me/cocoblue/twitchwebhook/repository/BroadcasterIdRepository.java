package me.cocoblue.twitchwebhook.repository;

import me.cocoblue.twitchwebhook.entity.BroadcasterId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BroadcasterIdRepository extends JpaRepository<BroadcasterId, Long> {

}
