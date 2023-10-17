package me.cocoblue.twitchwebhook.domain.twitch;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.Column;
import javax.persistence.Entity;

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
