package me.cocoblue.twitchwebhook.domain;


import lombok.Data;
import me.cocoblue.twitchwebhook.data.SubscriptionType;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@Immutable
@Table(name = "subscription_list_view")
public class SubscriptionListViewEntity implements Serializable {
    @EmbeddedId
    private subscriptionListViewId subscriptionListViewId;

    @Column(name="enabled")
    private boolean enabled;
}

@Data
@Embeddable
class subscriptionListViewId implements Serializable {
    @Column(name = "broadcaster_id")
    private long broadcasterId;

    @Enumerated(EnumType.STRING)
    @Column(name="type")
    private SubscriptionType subscriptionType;
}