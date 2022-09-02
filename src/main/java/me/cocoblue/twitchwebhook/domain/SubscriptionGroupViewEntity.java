package me.cocoblue.twitchwebhook.domain;


import lombok.Data;
import lombok.Getter;
import me.cocoblue.twitchwebhook.data.SubscriptionType;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Immutable
@Table(name = "subscription_group_view")
public class SubscriptionGroupViewEntity implements Serializable {
    @EmbeddedId
    private SubscriptionGroupViewId subscriptionGroupViewId;

    @Column(name="enabled", nullable = false, columnDefinition = "BIT", length = 1)
    private boolean enabled;

    public long getBroadcasterId() {
        return getSubscriptionGroupViewId().getBroadcasterId();
    }

    public SubscriptionType getSubscriptionType() {
        return getSubscriptionGroupViewId().getSubscriptionType();
    }
    public boolean getEnabled() {
        return enabled;
    }
}

@Data
@Embeddable
class SubscriptionGroupViewId implements Serializable {
    @Column(name = "broadcaster_id")
    private long broadcasterId;

    @Enumerated(EnumType.STRING)
    @Column(name="type")
    private SubscriptionType subscriptionType;
}