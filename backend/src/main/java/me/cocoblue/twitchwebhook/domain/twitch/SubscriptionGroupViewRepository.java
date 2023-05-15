package me.cocoblue.twitchwebhook.domain.twitch;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionGroupViewRepository extends JpaRepository<SubscriptionGroupViewEntity, SubscriptionGroupViewId> {
    List<SubscriptionGroupViewEntity> findAllByEnabled(boolean enabled);
    int countSubscriptionGroupViewEntitiesBySubscriptionGroupViewIdAndEnabled(SubscriptionGroupViewId subscriptionGroupViewId, boolean enabled);
}
