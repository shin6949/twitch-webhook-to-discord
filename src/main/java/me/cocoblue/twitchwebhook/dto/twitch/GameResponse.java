package me.cocoblue.twitchwebhook.dto.twitch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class GameResponse {
    @JsonProperty("data")
    private List<Game> gameList;
}
