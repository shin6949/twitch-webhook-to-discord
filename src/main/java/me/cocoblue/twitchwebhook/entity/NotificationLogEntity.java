package me.cocoblue.twitchwebhook.entity;

import com.sun.istack.NotNull;
import lombok.*;
import me.cocoblue.twitchwebhook.data.SubscriptionType;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Entity(name = "notification_log")
@Builder
public class NotificationLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 150)
    @NotNull
    private String idFromTwitch;
    @Enumerated(EnumType.STRING)
    @Column(name="type")
    @NotNull
    private SubscriptionType subscriptionType;
    @ManyToOne()
    @JoinColumn(name="broadcaster_id", foreignKey = @ForeignKey(name="FK_NOTIFICATION_LOG_BROADCASTER_ID"))
    @NotNull
    private BroadcasterIdEntity broadcasterIdEntity;
    @Column(updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime receivedTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        NotificationLogEntity that = (NotificationLogEntity) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public void test() {

    }
}
