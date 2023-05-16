package me.cocoblue.twitchwebhook.service.twitch;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.dto.twitch.AppTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Log4j2
@Service
@RequiredArgsConstructor
public class OauthTokenService {
    @Value("${twitch.client-id}")
    private String clientId;

    @Value("${twitch.client-secret}")
    private String clientSecret;

    @Value("${twitch.token-endpoint}")
    private String tokenEndpoint;

    public AppTokenResponse getAppTokenFromTwitch() {
        final RestTemplate restTemplate = new RestTemplate();
        final String tokenUrl = tokenEndpoint + "/token";
        final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(tokenUrl)
                .queryParam("client_id", clientId)
                .queryParam("client_secret", clientSecret)
                .queryParam("grant_type", "client_credentials");
        log.debug("built URI: " + builder.toUriString());

        final ResponseEntity<AppTokenResponse> responseEntity = restTemplate.postForEntity(builder.toUriString(), null, AppTokenResponse.class);
        return responseEntity.getBody();
    }

    @Async
    public void revokeAppTokenToTwitch(String appAccessToken) {
        log.info("Revoke App Access Token Started");
        log.debug("Revoking App Access Token: " + appAccessToken);
        final RestTemplate restTemplate = new RestTemplate();
        final String revokeUrl = tokenEndpoint + "/revoke";

        MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
        body.add("client_id", clientId);
        body.add("token", appAccessToken);
        log.debug("Parameter Data: " + body);

        restTemplate.postForEntity(revokeUrl, body, String.class);
        log.info("Revoke OAuth Token Successfully");
    }
}
