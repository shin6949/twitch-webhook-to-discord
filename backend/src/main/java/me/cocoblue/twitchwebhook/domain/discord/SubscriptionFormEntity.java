package me.cocoblue.twitchwebhook.domain.discord;

import lombok.*;
import lombok.experimental.SuperBuilder;
import me.cocoblue.twitchwebhook.domain.twitch.BroadcasterIdEntity;
import me.cocoblue.twitchwebhook.domain.twitch.FormEntity;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

@Getter
@Setter
@SuperBuilder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@Entity(name = "subscription_form")
public class SubscriptionFormEntity extends FormEntity {
    @ManyToOne()
    @JoinColumn(name="bot_profile_id", foreignKey = @ForeignKey(name="FK_SUBSCRIPTION_FORM_BOT_PROFILE_ID"), nullable = false)
    private BotProfileDataEntity botProfileId;

    @Column(length = 2000, name="content", nullable = false)
    private String content;

    @Column(name = "color_hex", nullable = false)
    private String colorHex;

    @ManyToOne()
    @JoinColumn(name="webhook_id", foreignKey = @ForeignKey(name="FK_SUBSCRIPTION_FORM_WEBHOOK_ID"), nullable = false)
    private WebhookDataEntity webhookId;

    // 누가 이 폼을 만들었는지
    @ManyToOne()
    @JoinColumn(name="form_owner", foreignKey = @ForeignKey(name="FK_SUBSCRIPTION_FORM_OWNER_BROADCASTER_ID"), nullable = false)
    private BroadcasterIdEntity formOwner;

    public int getDecimalColor() {
        return Integer.parseInt(getColorHex(),16);
    }
}
