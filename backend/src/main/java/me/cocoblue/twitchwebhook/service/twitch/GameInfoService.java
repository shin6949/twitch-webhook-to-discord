package me.cocoblue.twitchwebhook.service.twitch;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.dto.twitch.AppTokenResponse;
import me.cocoblue.twitchwebhook.dto.twitch.Game;
import me.cocoblue.twitchwebhook.dto.twitch.GameResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Log4j2
@RequiredArgsConstructor
public class GameInfoService {
    private final OauthTokenService oauthTokenService;
    private final RequestService requestService;

    @Value("${twitch.api-endpoint}")
    private String twitchApiUrl;


    public Game getGameInfoByIdFromTwitch(String gameId) {
        log.info("Getting game information");

        final AppTokenResponse appTokenResponse = oauthTokenService.getAppTokenFromTwitch();
        final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(twitchApiUrl + "/games")
                .queryParam("id", gameId);

        HttpEntity<?> entity = new HttpEntity<>(requestService.makeRequestHeader(appTokenResponse.getAccessToken()));

        RestTemplate rt = new RestTemplate();
        ResponseEntity<GameResponse> response;

        try {
            response = rt.exchange(builder.toUriString(), HttpMethod.GET, entity, GameResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        final GameResponse gameResponse = response.getBody();

        assert gameResponse != null;
        log.debug("Got Game Info: " + gameResponse);

        oauthTokenService.revokeAppTokenToTwitch(appTokenResponse.getAccessToken());
        return gameResponse.getGameList().get(0);
    }
}
