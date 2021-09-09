package me.cocoblue.twitchwebhook.service;

import lombok.RequiredArgsConstructor;
import me.cocoblue.twitchwebhook.dto.OauthToken;
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
public class TwitchHelixApiServiceImpl {
    private final OauthTokenService oauthTokenService;
    private final String userGetUrl = "https://api.twitch.tv/helix/users";
    @Value("${twitch.client-id}")
    private String clientId;

    public UserInfo requestUserInfoToTwitch(String AccessToken, UriComponentsBuilder builder) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Client-id", clientId);
        headers.add("Authorization", "Bearer " + AccessToken);
        HttpEntity<?> entity = new HttpEntity<>(headers);

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
