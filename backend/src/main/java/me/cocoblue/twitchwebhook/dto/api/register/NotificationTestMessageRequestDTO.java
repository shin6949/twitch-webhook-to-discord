package me.cocoblue.twitchwebhook.dto.api.register;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NotificationTestMessageRequestDTO {
    private String title;
    private String content;
    @JsonProperty("registration_token")
    private String registrationToken;
}
