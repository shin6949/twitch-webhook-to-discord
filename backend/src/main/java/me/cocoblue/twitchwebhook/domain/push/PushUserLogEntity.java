package me.cocoblue.twitchwebhook.domain.push;

import lombok.*;
import lombok.experimental.SuperBuilder;
import me.cocoblue.twitchwebhook.domain.twitch.UserLogSuperEntity;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

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
