package me.cocoblue.twitchwebhook.dto.discord.embed;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiscordFooter {
    @JsonProperty("text")
    private String text;
    @JsonProperty("icon_url")
    private String iconUrl;
}