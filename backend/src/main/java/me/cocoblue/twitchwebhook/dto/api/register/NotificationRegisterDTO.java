package me.cocoblue.twitchwebhook.dto.api.register;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.cocoblue.twitchwebhook.data.LanguageIsoData;
import me.cocoblue.twitchwebhook.data.TwitchSubscriptionType;
import me.cocoblue.twitchwebhook.domain.twitch.BroadcasterIdEntity;
import me.cocoblue.twitchwebhook.domain.twitch.PushSubscriptionFormEntity;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRegisterDTO {
    @JsonProperty("twitch_id")
    private String twitchId;
    @JsonProperty("notification_type")
    private String notificationType;
    @JsonProperty("interval_minute")
    private int intervalMinute;
    @JsonProperty("registration_token")
    private String registrationToken;
    private LanguageIsoData language;

    public PushSubscriptionFormEntity toEntity(BroadcasterIdEntity broadcasterIdEntity) {
        return PushSubscriptionFormEntity.builder()
                .registrationToken(registrationToken)
                .broadcasterIdEntity(broadcasterIdEntity)
                .twitchSubscriptionType(TwitchSubscriptionType.find(notificationType))
                .intervalMinute(intervalMinute)
                .languageIsoData(language)
                .enabled(false)
                .build();
    }
}
