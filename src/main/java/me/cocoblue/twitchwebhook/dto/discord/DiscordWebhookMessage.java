package me.cocoblue.twitchwebhook.dto.discord;

import me.cocoblue.twitchwebhook.dto.discord.embed.DiscordEmbed;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Repository
public class DiscordWebhookMessage {
    @JsonProperty("username")
    private String username;
    @JsonProperty("avatar_url")
    private String avatarUrl;
    @JsonProperty("content")
    private String content;
    @JsonProperty("embeds")
    private List<DiscordEmbed> embeds;
}





