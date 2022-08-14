package me.cocoblue.twitchwebhook.domain;

import com.sun.istack.NotNull;
import lombok.*;
import me.cocoblue.twitchwebhook.data.LanguageIsoData;
import me.cocoblue.twitchwebhook.data.SubscriptionType;

import javax.persistence.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
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

    @Column(length = 2000)
    @NotNull
    private String content;

    @NotNull
    private String colorHex;

    @Enumerated(EnumType.STRING)
    @Column(name="type")
    @NotNull
    private SubscriptionType subscriptionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "language")
    private LanguageIsoData languageIsoData;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="webhook_id", foreignKey = @ForeignKey(name="FK_SUBSCRIPTION_FORM_WEBHOOK_ID"))
    @NotNull
    private WebhookDataEntity webhookId;

    // 누가 이 폼을 만들었는지
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="form_owner", foreignKey = @ForeignKey(name="FK_SUBSCRIPTION_FORM_OWNER_BROADCASTER_ID"))
    private BroadcasterIdEntity formOwner;

    public int getDecimalColor() {
        return Integer.parseInt(getColorHex(),16);
    }
}
