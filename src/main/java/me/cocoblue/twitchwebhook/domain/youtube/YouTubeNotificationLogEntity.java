package me.cocoblue.twitchwebhook.domain.youtube;

import com.sun.istack.NotNull;
import lombok.*;
import me.cocoblue.twitchwebhook.data.YouTubeSubscriptionType;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@DynamicInsert
@Entity(name = "youtube_notification_log")
public class YouTubeNotificationLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name="type")
    @NotNull
    private YouTubeSubscriptionType youTubeSubscriptionType;

    @ManyToOne()
    @JoinColumn(name="channel_id", foreignKey = @ForeignKey(name="FK_YOUTUBE_NOTIFICATION_LOG_CHANNEL_ID"))
    @NotNull
    private YouTubeChannelInfoEntity youTubeChannelInfoEntity;

    @Column(name = "video_id", length = 150)
    @NotNull
    private String videoId;

    @Column(name = "received_time", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime receivedTime;
}
