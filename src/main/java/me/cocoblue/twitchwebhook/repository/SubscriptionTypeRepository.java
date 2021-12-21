package me.cocoblue.twitchwebhook.repository;

import me.cocoblue.twitchwebhook.entity.SubscriptionTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionTypeRepository extends JpaRepository<SubscriptionTypeEntity, Long> {
    SubscriptionTypeEntity getSubscriptionTypeEntityByNameEquals(String name);
}
