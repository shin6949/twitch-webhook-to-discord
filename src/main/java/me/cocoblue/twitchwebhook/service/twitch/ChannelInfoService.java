package me.cocoblue.twitchwebhook.service.twitch;

import me.cocoblue.twitchwebhook.vo.twitch.Channel;

public interface ChannelInfoService {
    Channel getChannelInformationByBroadcasterId(String broadcasterId);
}
