package me.cocoblue.twitchwebhook.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "notification_log")
@Builder
public class NotificationLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 150)
    private String idFromTwitch;
    @ManyToOne()
    @JoinColumn(name="broadcaster_id", foreignKey = @ForeignKey(name="FK_STREAM_NOTIFY_LOG_BROADCASTER_ID"))
    private BroadcasterIdEntity broadcasterIdEntity;
    @Column(length = 300)
    private String title;
    private LocalDateTime startedAt;
    @ManyToOne()
    @JoinColumn(name="game_id", foreignKey = @ForeignKey(name="FK_STREAM_NOTIFY_LOG_GAME_ID"))
    private GameIndexEntity gameIndexEntity;
    @Column(updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime recordTimeToDb;

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
}
