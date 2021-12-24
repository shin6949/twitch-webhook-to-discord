package me.cocoblue.twitchwebhook.service;

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

@Log4j2
@Service
@RequiredArgsConstructor
public class OauthTokenServiceImpl implements OauthTokenService {
    @Value("${twitch.client-id}")
    private String clientId;

    @Value("${twitch.client-secret}")
    private String clientSecret;

    @Override
    public AppTokenResponse getAppTokenFromTwitch() {
        final RestTemplate restTemplate = new RestTemplate();
        final String tokenUrl = "https://id.twitch.tv/oauth2/token";

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
        parameters.add("client_id", clientId);
        parameters.add("client_secret", clientSecret);
        parameters.add("grant_type", "client_credentials");

        final ResponseEntity<AppTokenResponse> responseEntity = restTemplate.postForEntity(tokenUrl, parameters, AppTokenResponse.class);
        return responseEntity.getBody();
    }

    @Override
    @Async
    public void revokeAppTokenToTwitch(String appAccessToken) {
        final RestTemplate restTemplate = new RestTemplate();
        final String revokeUrl = "https://id.twitch.tv/oauth2/revoke";

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
        parameters.add("client_id", clientId);
        parameters.add("token", appAccessToken);

        restTemplate.postForEntity(revokeUrl, parameters, String.class);
        log.info("Revoke OAuth Token Successfully");
    }
}
