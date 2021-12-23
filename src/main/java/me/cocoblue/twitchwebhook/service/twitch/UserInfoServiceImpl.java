package me.cocoblue.twitchwebhook.service.twitch;

import lombok.RequiredArgsConstructor;
import me.cocoblue.twitchwebhook.dto.twitch.AppTokenResponse;
import me.cocoblue.twitchwebhook.entity.BroadcasterIdEntity;
import me.cocoblue.twitchwebhook.repository.BroadcasterIdRepository;
import me.cocoblue.twitchwebhook.service.OauthTokenService;
import me.cocoblue.twitchwebhook.dto.twitch.UserResponse;
import me.cocoblue.twitchwebhook.dto.twitch.User;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class UserInfoServiceImpl implements UserInfoService {
    private final BroadcasterIdRepository broadcasterIdRepository;
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
        final AppTokenResponse appTokenResponse = oauthTokenService.getAppTokenFromTwitch();
        final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(userGetUrl)
                .queryParam("login", loginId);

        final UserResponse userResponse = requestUserInfoToTwitch(appTokenResponse.getAccessToken(), builder);

        oauthTokenService.revokeAppTokenToTwitch(appTokenResponse.getAccessToken());
        updateUserToDb(userResponse.getTwitchUsers().get(0));
        return userResponse.getTwitchUsers().get(0);
    }

    @Override
    public User getUserInfoByBroadcasterIdFromTwitch(String broadcasterId) {
        final AppTokenResponse appTokenResponse = oauthTokenService.getAppTokenFromTwitch();

        final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(userGetUrl)
                .queryParam("id", broadcasterId);

        final UserResponse userResponse = requestUserInfoToTwitch(appTokenResponse.getAccessToken(), builder);

        oauthTokenService.revokeAppTokenToTwitch(appTokenResponse.getAccessToken());
        updateUserToDb(userResponse.getTwitchUsers().get(0));
        return userResponse.getTwitchUsers().get(0);
    }

    @Async
    protected void updateUserToDb(User user) {
        final BroadcasterIdEntity userFromDb = broadcasterIdRepository.getBroadcasterIdEntityByIdEquals(user.getId());

        if(!user.toBroadcasterIdEntity().equals(userFromDb)) {
            broadcasterIdRepository.save(user.toBroadcasterIdEntity());
        }
    }

}
