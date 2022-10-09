package me.cocoblue.twitchwebhook.domain.youtube;

import com.sun.istack.NotNull;
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

    @Column(length = 150)
    @NotNull
    private String channelId;

    @Column(length = 150)
    @NotNull
    private String videoId;

    @Column(updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime receivedTime;
}
