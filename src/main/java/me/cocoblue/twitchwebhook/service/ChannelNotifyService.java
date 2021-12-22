package me.cocoblue.twitchwebhook.service;

import me.cocoblue.twitchwebhook.dto.twitch.Channel;
import me.cocoblue.twitchwebhook.dto.twitch.eventsub.ChannelUpdateRequest;

public interface ChannelNotifyService {
    void sendChannelUpdateMessage(ChannelUpdateRequest.Body body, Channel channel);
}
