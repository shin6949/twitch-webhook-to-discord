package me.cocoblue.twitchwebhook.domain;

import com.sun.istack.NotNull;
import lombok.*;
import me.cocoblue.twitchwebhook.data.TwitchSubscriptionType;
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
@Entity(name = "user_log_view")
public class UserLogViewEntity {
    @Id
    @Column(name = "user_log_id")
    private Long userLogId;

    @Column(name = "received_time")
    @NotNull
    private LocalDateTime receivedTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TwitchSubscriptionType twitchSubscriptionType;

    @ManyToOne()
    @JoinColumn(name="form_id")
    @NotNull
    private SubscriptionFormEntity subscriptionFormEntity;

    @ManyToOne()
    @JoinColumn(name="broadcaster_id")
    @NotNull
    private BroadcasterIdEntity broadcasterIdEntity;
}
