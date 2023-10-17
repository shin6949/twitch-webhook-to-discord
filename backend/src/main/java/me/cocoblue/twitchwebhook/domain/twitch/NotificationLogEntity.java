package me.cocoblue.twitchwebhook.domain.twitch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.cocoblue.twitchwebhook.data.TwitchSubscriptionType;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "notification_log")
public class NotificationLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 150, nullable = false)
    private String idFromTwitch;

    @Enumerated(EnumType.STRING)
    @Column(name="type", nullable = false)
    private TwitchSubscriptionType twitchSubscriptionType;

    @ManyToOne()
    @JoinColumn(name="broadcaster_id", foreignKey = @ForeignKey(name="FK_NOTIFICATION_LOG_BROADCASTER_ID"), nullable = false)
    private BroadcasterIdEntity broadcasterIdEntity;

    @Column(name = "received_time", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", nullable = false)
    private LocalDateTime receivedTime;
}
