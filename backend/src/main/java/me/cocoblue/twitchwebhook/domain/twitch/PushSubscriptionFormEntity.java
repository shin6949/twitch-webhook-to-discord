package me.cocoblue.twitchwebhook.domain.twitch;

import lombok.*;
import lombok.experimental.SuperBuilder;
import me.cocoblue.twitchwebhook.domain.twitch.FormEntity;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@Entity(name = "push_subscription_form")
public class PushSubscriptionFormEntity extends FormEntity {
    @ManyToOne()
    @JoinColumn(name="registration_uuid", foreignKey = @ForeignKey(name="FK_PUSH_SUBSCRIPTION_FORM_REGISTRATION_UUID"), nullable = false)
    private PushUUIDStorageEntity registrationUUID;
}
