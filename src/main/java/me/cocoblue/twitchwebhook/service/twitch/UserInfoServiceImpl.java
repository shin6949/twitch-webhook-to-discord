package me.cocoblue.twitchwebhook.service.twitch;

import lombok.RequiredArgsConstructor;
import me.cocoblue.twitchwebhook.dto.OauthToken;
import me.cocoblue.twitchwebhook.service.OauthTokenService;
import me.cocoblue.twitchwebhook.vo.UserInfo;
import me.cocoblue.twitchwebhook.vo.twitch.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class UserInfoServiceImpl implements UserInfoService {
    private final OauthTokenService oauthTokenService;
    private final RequestService requestService;

    private final String userGetUrl = "https://api.twitch.tv/helix/users";

    @Override
    public UserInfo requestUserInfoToTwitch(String accessToken, UriComponentsBuilder builder) {
        HttpEntity<?> entity = requestService.makeRequestHeader(accessToken);

        RestTemplate rt = new RestTemplate();
        ResponseEntity<UserInfo> response;

        try {
            response = rt.exchange(builder.toUriString(), HttpMethod.GET, entity, UserInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return response.getBody();
    }

    @Override
    public User getUserInfoByLoginIdFromTwitch(String loginId) {
        OauthToken oauthToken = oauthTokenService.getRecentOauthToken();
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(userGetUrl)
                .queryParam("login", loginId);

        UserInfo userInfo = requestUserInfoToTwitch(oauthToken.getAccessToken(), builder);
        if (userInfo == null) {
            OauthToken refreshToken = oauthTokenService.getOauthTokenFromTwitch();
            userInfo = requestUserInfoToTwitch(refreshToken.getAccessToken(), builder);
        }

        return userInfo.getTwitchUsers().get(0);
    }

    @Override
    public User getUserInfoByBroadcasterIdFromTwitch(String broadcasterId) {
        OauthToken oauthToken = oauthTokenService.getRecentOauthToken();

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(userGetUrl)
                .queryParam("id", broadcasterId);

        UserInfo userInfo = requestUserInfoToTwitch(oauthToken.getAccessToken(), builder);
        if (userInfo == null) {
            OauthToken refreshToken = oauthTokenService.getOauthTokenFromTwitch();
            userInfo = requestUserInfoToTwitch(refreshToken.getAccessToken(), builder);
        }

        return userInfo.getTwitchUsers().get(0);
    }
}
