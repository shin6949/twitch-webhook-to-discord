package me.cocoblue.twitchwebhook.dto.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.cocoblue.twitchwebhook.data.LanguageIsoData;
import me.cocoblue.twitchwebhook.data.TwitchSubscriptionType;
import me.cocoblue.twitchwebhook.domain.twitch.BroadcasterIdEntity;
import me.cocoblue.twitchwebhook.domain.push.PushSubscriptionFormEntity;

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
    private LanguageIsoData language;

    public PushSubscriptionFormEntity toEntity(BroadcasterIdEntity broadcasterIdEntity) {
        return PushSubscriptionFormEntity.builder()
                .registrationToken(registrationToken)
                .broadcasterIdEntity(broadcasterIdEntity)
                .twitchSubscriptionType(TwitchSubscriptionType.find(notificationType))
                .intervalMinute(delayTime)
                .languageIsoData(language)
                .enabled(false)
                .build();
    }
}
