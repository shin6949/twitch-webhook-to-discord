package me.cocoblue.twitchwebhook.dto.twitch.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Condition {
    @JsonProperty("broadcaster_user_id")
    private String broadcasterUserId;
}
