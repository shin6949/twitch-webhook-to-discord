package me.cocoblue.twitchwebhook.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionGroupViewRepository extends JpaRepository<SubscriptionGroupViewEntity, SubscriptionGroupViewId> {
    List<SubscriptionGroupViewEntity> findAllByEnabled(boolean enabled);
}
