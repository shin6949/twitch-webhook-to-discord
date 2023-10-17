package me.cocoblue.twitchwebhook.domain.logdomain;

import lombok.*;
import me.cocoblue.twitchwebhook.data.TwitchSubscriptionType;
import me.cocoblue.twitchwebhook.domain.twitch.BroadcasterIdEntity;
import me.cocoblue.twitchwebhook.domain.twitch.PushSubscriptionFormEntity;
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
@Entity(name = "push_user_log_view")
public class PushUserLogViewEntity {
    @Id
    @Column(name = "user_log_id")
    private Long userLogId;

    @Column(name = "received_time", nullable = false)
    private LocalDateTime receivedTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TwitchSubscriptionType twitchSubscriptionType;

    @ManyToOne()
    @JoinColumn(name="form_id", nullable = false)
    private PushSubscriptionFormEntity pushSubscriptionFormEntity;

    @ManyToOne()
    @JoinColumn(name="broadcaster_id", nullable = false)
    private BroadcasterIdEntity broadcasterIdEntity;
}
