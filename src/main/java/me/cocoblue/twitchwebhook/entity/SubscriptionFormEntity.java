package me.cocoblue.twitchwebhook.entity;

import lombok.*;
import me.cocoblue.twitchwebhook.dto.SubscriptionType;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Entity(name = "subscription_form")
public class SubscriptionFormEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="broadcaster_id", foreignKey = @ForeignKey(name="FK_SUBSCRIPTION_FORM_BROADCASTER_ID"))
    private BroadcasterIdEntity broadcasterIdEntity;
    @Column(length = 100)
    private String username;
    @Column(length = 600)
    private String avatarUrl;
    @Column(length = 2000)
    private String content;
    private int color;
    @Enumerated(EnumType.STRING)
    @Column(name="type")
    private SubscriptionType subscriptionType;
    @Column(length = 500)
    private String webhookUrl;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        SubscriptionFormEntity that = (SubscriptionFormEntity) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
