package me.cocoblue.twitchwebhook.dto.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.cocoblue.twitchwebhook.domain.BroadcasterIdEntity;
import me.cocoblue.twitchwebhook.domain.push.PushSubscriptionFormEntity;

import java.util.Locale;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRegisterDTO {
    @JsonProperty("twitch_id")
    private String twitchId;
    @JsonProperty("notification_type")
    private String notificationType;
    @JsonProperty("delay_time")
    private int delayTime;
    @JsonProperty("registration_token")
    private String registrationToken;
    private Locale language;

    public PushSubscriptionFormEntity toEntity(BroadcasterIdEntity broadcasterIdEntity) {
        return PushSubscriptionFormEntity.builder()
                .registrationToken(registrationToken)
                .broadcasterIdEntity(broadcasterIdEntity)
                .intervalMinute(delayTime)
                .enabled(false)
                .build();
    }
}
