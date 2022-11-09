package me.cocoblue.twitchwebhook.domain.youtube;

import com.sun.istack.NotNull;
import lombok.*;
import me.cocoblue.twitchwebhook.data.LanguageIsoData;
import me.cocoblue.twitchwebhook.data.YouTubeSubscriptionType;
import me.cocoblue.twitchwebhook.domain.BotProfileDataEntity;
import me.cocoblue.twitchwebhook.domain.BroadcasterIdEntity;
import me.cocoblue.twitchwebhook.domain.WebhookDataEntity;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@DynamicInsert
@Entity(name = "youtube_subscription_form")
public class YouTubeSubscriptionFormEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne()
    @JoinColumn(name="channel_id", foreignKey = @ForeignKey(name="FK_YOUTUBE_SUBSCRIPTION_FORM_CHANNEL_ID"))
    @NotNull
    private YouTubeChannelInfoEntity youTubeChannelInfoEntity;

    @Column(length = 2000, name="content")
    private String content;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="bot_profile_id", foreignKey = @ForeignKey(name="FK_YOUTUBE_SUBSCRIPTION_FORM_BOT_PROFILE_ID"))
    @NotNull
    private BotProfileDataEntity botProfileId;

    @Enumerated(EnumType.STRING)
    @Column(name="type")
    @NotNull
    private YouTubeSubscriptionType youTubeSubscriptionType;

    @Column(name = "color_hex", nullable = false)
    private String colorHex;

    @Enumerated(EnumType.STRING)
    @Column(name = "language", nullable = false)
    private LanguageIsoData languageIsoData;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="webhook_id", foreignKey = @ForeignKey(name="FK_YOUTUBE_SUBSCRIPTION_FORM_WEBHOOK_ID"))
    @NotNull
    private WebhookDataEntity webhookId;

    // 누가 이 폼을 만들었는지
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="form_owner", foreignKey = @ForeignKey(name="FK_YOUTUBE_SUBSCRIPTION_FORM_OWNER_BROADCASTER_ID"))
    @NotNull
    private BroadcasterIdEntity formOwner;

    @Column(name="created_at")
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name="avoid_duplicate_suspicion_noti", nullable = false, columnDefinition = "BIT", length = 1)
    @ColumnDefault("true")
    private boolean avoidDuplicateSuspicionNoti;

    @Column(name="enabled", nullable = false, columnDefinition = "BIT", length = 1)
    @ColumnDefault("false")
    private boolean enabled;

    public int getDecimalColor() {
        return Integer.parseInt(getColorHex(),16);
    }
}
