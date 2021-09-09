package me.cocoblue.twitchwebhook.service;

import me.cocoblue.twitchwebhook.dto.discord.DiscordEmbed;
import me.cocoblue.twitchwebhook.dto.discord.DiscordWebhookMessage;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DiscordMessageService {
    public DiscordWebhookMessage makeWebhookMessage(String username, String avatarUrl, String content,
                                                    DiscordEmbed embed) {
        List<DiscordEmbed> embeds = new ArrayList<>();
        embeds.add(embed);

        return new DiscordWebhookMessage(username, avatarUrl, content, embeds);
    }

    public DiscordEmbed makeDiscordEmbed() {
        return new DiscordEmbed();
    }
}
