package me.cocoblue.twitchwebhook.dto.twitch.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Condition {
    @JsonProperty("broadcaster_user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String broadcasterUserId;
}
