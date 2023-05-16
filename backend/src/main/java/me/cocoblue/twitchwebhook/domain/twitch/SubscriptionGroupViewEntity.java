package me.cocoblue.twitchwebhook.domain.twitch;


import lombok.Data;
import lombok.Getter;
import me.cocoblue.twitchwebhook.data.TwitchSubscriptionType;
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

    @Column(name="enabled", nullable = false, columnDefinition = "tinyint(1) default 0")
    private boolean enabled;

    public long getBroadcasterId() {
        return getSubscriptionGroupViewId().getBroadcasterId();
    }

    public TwitchSubscriptionType getSubscriptionType() {
        return getSubscriptionGroupViewId().getTwitchSubscriptionType();
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
    private TwitchSubscriptionType twitchSubscriptionType;
}
