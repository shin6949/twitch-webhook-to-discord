package me.cocoblue.twitchwebhook.domain.logdomain;

import lombok.*;
import lombok.experimental.SuperBuilder;
import me.cocoblue.twitchwebhook.domain.twitch.PushSubscriptionFormEntity;
import me.cocoblue.twitchwebhook.domain.twitch.UserLogSuperEntity;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@Entity(name = "push_user_log")
public class PushUserLogEntity extends UserLogSuperEntity {
    @ManyToOne()
    @JoinColumn(name="form_id", foreignKey = @ForeignKey(name="FK_PUSH_USER_LOG_FORM_ID"), nullable = false)
    private PushSubscriptionFormEntity pushSubscriptionFormEntity;
}
