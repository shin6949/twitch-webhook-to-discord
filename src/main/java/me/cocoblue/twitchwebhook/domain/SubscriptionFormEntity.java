package me.cocoblue.twitchwebhook.domain;

import com.sun.istack.NotNull;
import lombok.*;
import me.cocoblue.twitchwebhook.data.LanguageIsoData;
import me.cocoblue.twitchwebhook.data.TwitchSubscriptionType;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@DynamicInsert
@Entity(name = "subscription_form")
public class SubscriptionFormEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 누구를 안내할지
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="broadcaster_id", foreignKey = @ForeignKey(name="FK_SUBSCRIPTION_FORM_BROADCASTER_ID"))
    @NotNull
    private BroadcasterIdEntity broadcasterIdEntity;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="bot_profile_id", foreignKey = @ForeignKey(name="FK_SUBSCRIPTION_FORM_BOT_PROFILE_ID"))
    @NotNull
    private BotProfileDataEntity botProfileId;

    @Column(length = 2000, name="content", nullable = false)
    private String content;

    @Column(name = "color_hex", nullable = false)
    private String colorHex;

    @Enumerated(EnumType.STRING)
    @Column(name="type", nullable = false)
    private TwitchSubscriptionType twitchSubscriptionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "language", nullable = false)
    private LanguageIsoData languageIsoData;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="webhook_id", foreignKey = @ForeignKey(name="FK_SUBSCRIPTION_FORM_WEBHOOK_ID"))
    @NotNull
    private WebhookDataEntity webhookId;

    // 누가 이 폼을 만들었는지
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="form_owner", foreignKey = @ForeignKey(name="FK_SUBSCRIPTION_FORM_OWNER_BROADCASTER_ID"))
    @NotNull
    private BroadcasterIdEntity formOwner;

    @Column(name="created_at")
    @NotNull
    private LocalDateTime createdAt;

    @Column(name="avoid_duplicate_noti", nullable = false, columnDefinition = "BIT", length = 1)
    @ColumnDefault("true")
    private boolean avoidDuplicateNoti;

    @Column(name="enabled", nullable = false, columnDefinition = "BIT", length = 1)
    @ColumnDefault("false")
    private boolean enabled;

    public int getDecimalColor() {
        return Integer.parseInt(getColorHex(),16);
    }
}
