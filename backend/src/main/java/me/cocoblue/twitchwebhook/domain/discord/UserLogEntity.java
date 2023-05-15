package me.cocoblue.twitchwebhook.domain.discord;

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
@Entity(name = "user_log")
public class UserLogEntity extends UserLogSuperEntity {
    @ManyToOne()
    @JoinColumn(name="form_id", foreignKey = @ForeignKey(name="FK_USER_LOG_FORM_ID"), nullable = false)
    private SubscriptionFormEntity subscriptionFormEntity;
}
