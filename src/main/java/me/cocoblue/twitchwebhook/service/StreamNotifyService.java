package me.cocoblue.twitchwebhook.service;

import me.cocoblue.twitchwebhook.dto.twitch.Channel;
import me.cocoblue.twitchwebhook.dto.twitch.eventsub.StreamNotifyRequest;

public interface StreamNotifyService {
    void sendMessage(StreamNotifyRequest.Body body, Channel channel);
    void insertLog(StreamNotifyRequest.Event event, Channel channel);
}
