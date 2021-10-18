package me.cocoblue.twitchwebhook.vo.twitch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.cocoblue.twitchwebhook.dto.GameIndex;
import me.cocoblue.twitchwebhook.entity.GameIndexEntity;

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

    public GameIndexEntity toGameIndexEntity() {
        return GameIndexEntity.builder()
                .id(Long.parseLong(getGameId()))
                .name(getGameName())
                .build();
    }
}
