package me.cocoblue.twitchwebhook.service;

import me.cocoblue.twitchwebhook.vo.UserInfo;
import me.cocoblue.twitchwebhook.vo.twitch.User;
import org.springframework.web.util.UriComponentsBuilder;

public interface TwitchHelixApiService {
    UserInfo requestUserInfoToTwitch(String accessToken, UriComponentsBuilder builder);

    User getUserInfoByLoginIdFromTwitch(String loginId);

    User getUserInfoByBroadcasterIdFromTwitch(String broadcasterId);
}
