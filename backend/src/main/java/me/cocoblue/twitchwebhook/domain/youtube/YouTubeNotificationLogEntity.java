package me.cocoblue.twitchwebhook.domain.youtube;

import lombok.*;
import me.cocoblue.twitchwebhook.data.YouTubeSubscriptionType;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@Entity(name = "youtube_notification_log")
public class YouTubeNotificationLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name="type", nullable = false)
    private YouTubeSubscriptionType youTubeSubscriptionType;

    @ManyToOne()
    @JoinColumn(name="channel_id", foreignKey = @ForeignKey(name="FK_YOUTUBE_NOTIFICATION_LOG_CHANNEL_ID"), nullable = false)
    private YouTubeChannelInfoEntity youTubeChannelInfoEntity;

    @Column(name = "video_id", length = 150, nullable = false)
    private String videoId;

    @Column(name = "received_time", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime receivedTime;
}
