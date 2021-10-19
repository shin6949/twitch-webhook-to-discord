package me.cocoblue.twitchwebhook.service;

import me.cocoblue.twitchwebhook.vo.twitch.Channel;
import me.cocoblue.twitchwebhook.vo.twitch.eventsub.Body;
import me.cocoblue.twitchwebhook.vo.twitch.eventsub.Event;

public interface StreamNotifyService {
    void sendMessage(Body body, Channel channel);
    void insertLog(Event event, Channel channel);
}
