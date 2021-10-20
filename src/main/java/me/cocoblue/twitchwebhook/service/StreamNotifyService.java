package me.cocoblue.twitchwebhook.service;

import me.cocoblue.twitchwebhook.dto.twitch.Channel;
import me.cocoblue.twitchwebhook.dto.twitch.eventsub.Body;
import me.cocoblue.twitchwebhook.dto.twitch.eventsub.Event;

public interface StreamNotifyService {
    void sendMessage(Body body, Channel channel);
    void insertLog(Event event, Channel channel);
}
