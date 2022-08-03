package me.cocoblue.twitchwebhook.dto.twitch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Game {
    @JsonProperty("id")
    private String id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("box_art_url")
    private String boxArtUrl;

    public String removeSizeMentionInBoxArtUrl() {
        return getBoxArtUrl().replace("-{width}x{height}", "");
    }
}
