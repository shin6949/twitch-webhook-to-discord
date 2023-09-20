package me.cocoblue.twitchwebhook.domain.notion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotionDatabaseIndexRepository extends JpaRepository<NotionDatabaseIndexEntity, String> {
}
