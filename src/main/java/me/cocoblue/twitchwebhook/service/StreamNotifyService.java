package me.cocoblue.twitchwebhook.service;

import me.cocoblue.twitchwebhook.dto.Form;
import me.cocoblue.twitchwebhook.dto.discord.DiscordWebhookMessage;
import me.cocoblue.twitchwebhook.vo.twitch.Channel;
import me.cocoblue.twitchwebhook.vo.twitch.eventsub.Event;

public interface StreamNotifyService {
    void sendStartMessage(Event event, Channel channel);
    DiscordWebhookMessage makeEndDiscordWebhookMessage(String broadcasterId, Form form);
    void sendEndMessage(String broadcasterId);
    void sendDiscordWebHook(DiscordWebhookMessage discordWebhookMessage, String webhookUrl);
    void insertLog(Event event, Channel channel);
}
