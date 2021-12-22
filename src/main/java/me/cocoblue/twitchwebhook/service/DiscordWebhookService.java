package me.cocoblue.twitchwebhook.service;

import me.cocoblue.twitchwebhook.dto.discord.DiscordEmbed;

public interface DiscordWebhookService {
    void send(DiscordEmbed.Webhook discordWebhookMessage, String webhookUrl);
}
