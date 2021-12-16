package me.cocoblue.twitchwebhook.service.twitch;

import lombok.RequiredArgsConstructor;
import me.cocoblue.twitchwebhook.entity.OauthTokenEntity;
import me.cocoblue.twitchwebhook.service.OauthTokenService;
import me.cocoblue.twitchwebhook.dto.twitch.UserResponse;
import me.cocoblue.twitchwebhook.dto.twitch.User;
import org.springframework.http.HttpEntity;
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
    public UserResponse requestUserInfoToTwitch(String accessToken, UriComponentsBuilder builder) {
        HttpEntity<?> entity = new HttpEntity<>(requestService.makeRequestHeader(accessToken));

        RestTemplate rt = new RestTemplate();
        ResponseEntity<UserResponse> response;

        try {
            response = rt.exchange(builder.toUriString(), HttpMethod.GET, entity, UserResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return response.getBody();
    }

    @Override
    public User getUserInfoByLoginIdFromTwitch(String loginId) {
        OauthTokenEntity oauthTokenEntity = oauthTokenService.getRecentOauthToken();
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(userGetUrl)
                .queryParam("login", loginId);

        UserResponse userResponse = requestUserInfoToTwitch(oauthTokenEntity.getAccessToken(), builder);
        if (userResponse == null) {
            OauthTokenEntity refreshToken = oauthTokenService.getOauthTokenFromTwitch();
            userResponse = requestUserInfoToTwitch(refreshToken.getAccessToken(), builder);
        }

        return userResponse.getTwitchUsers().get(0);
    }

    @Override
    public User getUserInfoByBroadcasterIdFromTwitch(String broadcasterId) {
        OauthTokenEntity oauthTokenEntity = oauthTokenService.getRecentOauthToken();

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(userGetUrl)
                .queryParam("id", broadcasterId);

        UserResponse userResponse = requestUserInfoToTwitch(oauthTokenEntity.getAccessToken(), builder);
        if (userResponse == null) {
            OauthTokenEntity refreshToken = oauthTokenService.getOauthTokenFromTwitch();
            userResponse = requestUserInfoToTwitch(refreshToken.getAccessToken(), builder);
        }

        return userResponse.getTwitchUsers().get(0);
    }
}