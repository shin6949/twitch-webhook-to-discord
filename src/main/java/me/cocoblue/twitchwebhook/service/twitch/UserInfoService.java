package me.cocoblue.twitchwebhook.service.twitch;

import me.cocoblue.twitchwebhook.vo.twitch.UserResponse;
import me.cocoblue.twitchwebhook.vo.twitch.User;
import org.springframework.web.util.UriComponentsBuilder;

public interface UserInfoService {
    UserResponse requestUserInfoToTwitch(String accessToken, UriComponentsBuilder builder);
    User getUserInfoByLoginIdFromTwitch(String loginId);
    User getUserInfoByBroadcasterIdFromTwitch(String broadcasterId);
}
