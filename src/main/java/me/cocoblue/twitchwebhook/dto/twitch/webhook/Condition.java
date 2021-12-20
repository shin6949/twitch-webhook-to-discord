package me.cocoblue.twitchwebhook.dto.twitch.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Condition {
    @JsonProperty("broadcaster_user_id")
    private String broadcasterUserId;

    public Condition(int broadcasterId) {
        this.broadcasterUserId = Integer.toString(broadcasterId);
    }
}
