package me.cocoblue.twitchwebhook.service;

import me.cocoblue.twitchwebhook.dto.Form;
import me.cocoblue.twitchwebhook.dto.discord.DiscordWebhookMessage;
import me.cocoblue.twitchwebhook.vo.twitch.notification.Stream;

public interface StreamNotifyService {
    DiscordWebhookMessage makeStartDiscordWebhookMessage(Stream stream, Form form);
    void sendStartMessage(Stream stream);
    DiscordWebhookMessage makeEndDiscordWebhookMessage(String broadcasterId, Form form);
    void sendEndMessage(String broadcasterId);
    void sendDiscordWebHook(DiscordWebhookMessage discordWebhookMessage, String webhookUrl);
    void insertLog(Stream stream);
}
