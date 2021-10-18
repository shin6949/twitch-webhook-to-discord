package me.cocoblue.twitchwebhook.repository;

import me.cocoblue.twitchwebhook.entity.GameIndexEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameIndexRepository extends JpaRepository<GameIndexEntity, Long> {
}
