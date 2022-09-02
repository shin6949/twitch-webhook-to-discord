package me.cocoblue.twitchwebhook.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WebhookDataRepository extends JpaRepository<WebhookDataEntity, Long> {
}
