package me.cocoblue.twitchwebhook.service.twitch;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.domain.twitch.BroadcasterIdEntity;
import me.cocoblue.twitchwebhook.domain.twitch.BroadcasterIdRepository;
import me.cocoblue.twitchwebhook.dto.twitch.AppTokenResponse;
import me.cocoblue.twitchwebhook.dto.twitch.User;
import me.cocoblue.twitchwebhook.dto.twitch.UserResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.LockModeType;
import java.util.Optional;
import java.util.function.Function;

@Log4j2
@Service
@RequiredArgsConstructor
public class UserInfoService {
    private final BroadcasterIdRepository broadcasterIdRepository;
    private final OauthTokenService oauthTokenService;
    private final RequestService requestService;

    @Value("${twitch.api-endpoint}")
    private String twitchApiUrl;

    private Function<String, UriComponentsBuilder> queryParamFunction(String key) {
        return value -> UriComponentsBuilder.fromHttpUrl(twitchApiUrl + "/users").queryParam(key, value);
    }

    public Optional<User> getUserInfoByLoginIdFromTwitch(String loginId) {
        return getUserInfo(queryParamFunction("login").apply(loginId));
    }

    public Optional<User> getUserInfoByBroadcasterIdFromTwitch(String broadcasterId) {
        return getUserInfo(queryParamFunction("id").apply(broadcasterId));
    }

    public Optional<User> getUserInfo(final UriComponentsBuilder builder) {
        log.info("Getting user information from twitch");
        log.debug("Built URL: " + builder.toUriString());

        final AppTokenResponse appTokenResponse = oauthTokenService.getAppTokenFromTwitch();
        final UserResponse userResponse = requestUserInfoToTwitch(appTokenResponse.getAccessToken(), builder);
        oauthTokenService.revokeAppTokenToTwitch(appTokenResponse.getAccessToken());

        if (userResponse == null || userResponse.getTwitchUsers().isEmpty()) {
            log.warn("No user info found from Twitch");
            return Optional.empty();
        }

        final User user = userResponse.getTwitchUsers().get(0);
        updateUserToDb(user);

        return Optional.of(user);
    }

    @Async
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    void updateUserToDb(final User user) {
        final BroadcasterIdEntity entity = user.toBroadcasterIdEntity();
        log.debug("Saving entity to DB: {}", entity.toString());
        broadcasterIdRepository.findById(entity.getId()).ifPresentOrElse(
                oldValue -> {
                    if (!oldValue.equals(entity)) {
                        broadcasterIdRepository.save(entity);
                        log.info("User Info Updated!");
                    } else {
                        log.info("User Info isn't modified. Don't Update it.");
                    }
                },
                () -> {
                    broadcasterIdRepository.save(entity);
                    log.info("User not found in database");
                }
        );
    }

    private UserResponse requestUserInfoToTwitch(String accessToken, UriComponentsBuilder builder) {
        final HttpEntity<?> entity = new HttpEntity<>(requestService.makeRequestHeader(accessToken));

        final RestTemplate rt = new RestTemplate();
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
