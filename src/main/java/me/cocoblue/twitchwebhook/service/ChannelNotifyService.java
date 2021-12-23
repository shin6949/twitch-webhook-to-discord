package me.cocoblue.twitchwebhook.service;

import me.cocoblue.twitchwebhook.dto.twitch.eventsub.ChannelUpdateRequest;

public interface ChannelNotifyService {
    void sendChannelUpdateMessage(ChannelUpdateRequest.Body body);
}
