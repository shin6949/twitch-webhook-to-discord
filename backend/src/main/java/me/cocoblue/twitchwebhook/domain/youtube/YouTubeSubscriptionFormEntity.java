package me.cocoblue.twitchwebhook.domain.youtube;

import lombok.*;
import lombok.experimental.SuperBuilder;
import me.cocoblue.twitchwebhook.data.LanguageIsoData;
import me.cocoblue.twitchwebhook.data.YouTubeSubscriptionType;
import me.cocoblue.twitchwebhook.domain.discord.BotProfileDataEntity;
import me.cocoblue.twitchwebhook.domain.twitch.BroadcasterIdEntity;
import me.cocoblue.twitchwebhook.domain.discord.WebhookDataEntity;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@DynamicInsert
@Entity(name = "youtube_subscription_form")
public class YouTubeSubscriptionFormEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="channel_id", foreignKey = @ForeignKey(name="FK_YOUTUBE_SUBSCRIPTION_FORM_CHANNEL_ID"), nullable = false)
    private YouTubeChannelInfoEntity youTubeChannelInfoEntity;

    @Column(length = 2000, name="content")
    private String content;

    @ManyToOne()
    @JoinColumn(name="bot_profile_id", foreignKey = @ForeignKey(name="FK_YOUTUBE_SUBSCRIPTION_FORM_BOT_PROFILE_ID"), nullable = false)
    private BotProfileDataEntity botProfileId;

    @Enumerated(EnumType.STRING)
    @Column(name="type", nullable = false)
    private YouTubeSubscriptionType youTubeSubscriptionType;

    @Column(name = "color_hex", nullable = false)
    private String colorHex;

    @Enumerated(EnumType.STRING)
    @Column(name = "language", nullable = false)
    private LanguageIsoData languageIsoData;

    @ManyToOne()
    @JoinColumn(name="webhook_id", foreignKey = @ForeignKey(name="FK_YOUTUBE_SUBSCRIPTION_FORM_WEBHOOK_ID"), nullable = false)
    private WebhookDataEntity webhookId;

    // 누가 이 폼을 만들었는지
    @ManyToOne()
    @JoinColumn(name="form_owner", foreignKey = @ForeignKey(name="FK_YOUTUBE_SUBSCRIPTION_FORM_OWNER_BROADCASTER_ID"), nullable = false)
    private BroadcasterIdEntity formOwner;

    @Column(name="created_at")
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name="interval_minute", nullable = false, length = 11)
    @ColumnDefault("10")
    private int intervalMinute;

    @Column(name="enabled", nullable = false, columnDefinition = "BIT", length = 1)
    @ColumnDefault("false")
    private boolean enabled;

    public int getDecimalColor() {
        return Integer.parseInt(getColorHex(),16);
    }
}
