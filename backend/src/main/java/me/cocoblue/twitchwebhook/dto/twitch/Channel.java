package me.cocoblue.twitchwebhook.dto.twitch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Channel {
    @JsonProperty("broadcaster_id")
    private String broadcasterId;
    @JsonProperty("broadcaster_login")
    private String broadcasterLogin;
    @JsonProperty("broadcaster_name")
    private String broadcasterName;
    @JsonProperty("broadcaster_language")
    private String broadcasterLanguage;
    @JsonProperty("game_id")
    private String gameId;
    @JsonProperty("game_name")
    private String gameName;
    @JsonProperty("title")
    private String title;
    @JsonProperty("delay")
    private int delay;
}
