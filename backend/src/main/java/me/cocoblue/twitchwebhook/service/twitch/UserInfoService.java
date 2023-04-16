package me.cocoblue.twitchwebhook.service.twitch;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.domain.BroadcasterIdEntity;
import me.cocoblue.twitchwebhook.domain.BroadcasterIdRepository;
import me.cocoblue.twitchwebhook.dto.twitch.AppTokenResponse;
import me.cocoblue.twitchwebhook.dto.twitch.User;
import me.cocoblue.twitchwebhook.dto.twitch.UserResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class UserInfoService {
    private final BroadcasterIdRepository broadcasterIdRepository;
    private final OauthTokenService oauthTokenService;
    private final RequestService requestService;

    @Value("${twitch.api-endpoint}")
    private String twitchApiUrl;

    public User getUserInfoByLoginIdFromTwitch(String loginId) {
        log.info("Getting user information by login id from twitch");

        final AppTokenResponse appTokenResponse = oauthTokenService.getAppTokenFromTwitch();
        final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(twitchApiUrl + "/users")
                .queryParam("login", loginId);
        log.debug("Built URL: " + builder.toUriString());

        final UserResponse userResponse = requestUserInfoToTwitch(appTokenResponse.getAccessToken(), builder);
        assert userResponse != null;
        log.debug("Got User Info: " + userResponse);

        oauthTokenService.revokeAppTokenToTwitch(appTokenResponse.getAccessToken());
        updateUserToDb(userResponse.getTwitchUsers().get(0));

        return userResponse.getTwitchUsers().get(0);
    }

    public User getUserInfoByBroadcasterIdFromTwitch(String broadcasterId) {
        log.info("Getting user information by Broadcaster Id from twitch");

        final AppTokenResponse appTokenResponse = oauthTokenService.getAppTokenFromTwitch();

        final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(twitchApiUrl + "/users")
                .queryParam("id", broadcasterId);
        log.debug("Built URL: " + builder.toUriString());

        final UserResponse userResponse = requestUserInfoToTwitch(appTokenResponse.getAccessToken(), builder);
        assert userResponse != null;
        log.debug("Got User Info: " + userResponse);

        oauthTokenService.revokeAppTokenToTwitch(appTokenResponse.getAccessToken());
        updateUserToDb(userResponse.getTwitchUsers().get(0));
        return userResponse.getTwitchUsers().get(0);
    }

    @Async
    protected void updateUserToDb(User user) {
        final Optional<BroadcasterIdEntity> userFromDb = broadcasterIdRepository.getBroadcasterIdEntityByIdEquals(user.getId());
        log.debug("Got User Info From DB: " + userFromDb);

        if(userFromDb.isPresent() && !user.toBroadcasterIdEntity().equals(userFromDb.get())) {
            broadcasterIdRepository.save(user.toBroadcasterIdEntity());
            log.info("User Info Updated!");
        } else {
            log.info("User Info isn't modified. Don't Update it.");
        }
    }

    private UserResponse requestUserInfoToTwitch(String accessToken, UriComponentsBuilder builder) {
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
}
