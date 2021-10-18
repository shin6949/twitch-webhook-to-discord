package me.cocoblue.twitchwebhook.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "stream_notify_log")
@Builder
public class StreamNotifyLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 150)
    private String idFromTwitch;
    @ManyToOne()
    @JoinColumn(name="broadcaster_id", foreignKey = @ForeignKey(name="FK_STREAM_NOTIFY_LOG_BROADCASTER_ID"))
    private BroadcasterId broadcasterId;
    @Column(length = 300)
    private String title;
    private LocalDateTime startedAt;
    @ManyToOne()
    @JoinColumn(name="game_id", foreignKey = @ForeignKey(name="FK_STREAM_NOTIFY_LOG_GAME_ID"))
    private GameIndexEntity gameIndexEntity;
    @Column(updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime recordTimeToDb;
}
