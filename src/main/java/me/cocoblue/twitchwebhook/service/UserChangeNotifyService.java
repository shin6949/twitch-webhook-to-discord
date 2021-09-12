package me.cocoblue.twitchwebhook.service;

import me.cocoblue.twitchwebhook.dto.Form;
import me.cocoblue.twitchwebhook.dto.discord.DiscordWebhookMessage;
import me.cocoblue.twitchwebhook.vo.twitch.notification.UserChange;

public interface UserChangeNotifyService {
    DiscordWebhookMessage makeDiscordWebhookMessage(UserChange userChange, Form form);
    void sendDiscordWebHook(UserChange userChange);
}
