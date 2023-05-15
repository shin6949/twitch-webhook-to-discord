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
@Entity(name = "youtube_user_log_view")
public class YouTubeUserLogViewEntity {
    @Id
    @Column(name = "user_log_id")
    private Long userLogId;

    @Column(name = "received_time", nullable = false)
    private LocalDateTime receivedTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private YouTubeSubscriptionType youTubeSubscriptionType;

    @ManyToOne()
    @JoinColumn(name="form_id", nullable = false)
    private YouTubeSubscriptionFormEntity youTubeSubscriptionFormEntity;

    @ManyToOne()
    @JoinColumn(name="channel_id", nullable = false)
    private YouTubeChannelInfoEntity youTubeChannelInfoEntity;
}
