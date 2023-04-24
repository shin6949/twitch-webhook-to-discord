package me.cocoblue.fcmsample.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
