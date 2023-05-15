package me.cocoblue.twitchwebhook.domain.push;

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
    @Column(name = "registration_token", nullable = false)
    private String registrationToken;
}
