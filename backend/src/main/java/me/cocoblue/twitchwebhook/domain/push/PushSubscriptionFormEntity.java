package me.cocoblue.twitchwebhook.domain.push;

import com.sun.istack.NotNull;
import lombok.*;
import me.cocoblue.twitchwebhook.data.LanguageIsoData;
import me.cocoblue.twitchwebhook.data.TwitchSubscriptionType;
import me.cocoblue.twitchwebhook.domain.BroadcasterIdEntity;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@AllArgsConstructor
@DynamicInsert
@Entity(name = "push_subscription_form")
public class PushSubscriptionFormEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 누구를 안내할지
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="broadcaster_id", foreignKey = @ForeignKey(name="FK_PUSH_SUBSCRIPTION_FORM_BROADCASTER_ID"))
    @NotNull
    private BroadcasterIdEntity broadcasterIdEntity;

    @Enumerated(EnumType.STRING)
    @Column(name="type", nullable = false)
    private TwitchSubscriptionType twitchSubscriptionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "language", nullable = false)
    private LanguageIsoData languageIsoData;

    // 누가 이 폼을 만들었는지
    @NotNull
    @Column(name = "registration_token", nullable = false)
    private String registrationToken;

    @Column(name="created_at")
    @CreationTimestamp
    private Timestamp createdAt;

    @Column(name="interval_minute", nullable = false, length = 11)
    @ColumnDefault("10")
    private int intervalMinute;

    @Column(name="enabled", nullable = false, columnDefinition = "BIT", length = 1)
    @ColumnDefault("false")
    private boolean enabled;
}
