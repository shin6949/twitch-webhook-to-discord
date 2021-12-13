package me.cocoblue.twitchwebhook.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.dto.twitch.AppTokenResponse;
import me.cocoblue.twitchwebhook.dto.twitch.OauthRequestForm;
import me.cocoblue.twitchwebhook.dto.twitch.OauthTokenResponse;
import me.cocoblue.twitchwebhook.entity.OauthTokenEntity;
import me.cocoblue.twitchwebhook.repository.OauthTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Log4j2
@Service
@RequiredArgsConstructor
public class OauthTokenServiceImpl implements OauthTokenService {
    private final OauthTokenRepository oauthTokenRepository;

    @Value("${twitch.client-id}")
    private String clientId;

    @Value("${twitch.client-secret}")
    private String clientSecret;

    @Override
    public OauthTokenEntity getRecentOauthToken() {
        return oauthTokenRepository.findFirstByOrderByCreateDateDesc();
    }

    @Override
    public void insertOauthToken(OauthTokenEntity oauthTokenEntity) {
        oauthTokenRepository.save(oauthTokenEntity);
    }

    @Override
    public OauthTokenEntity getOauthTokenFromTwitch() {
        OauthTokenEntity oauthTokenEntity = getRecentOauthToken();
        OauthRequestForm oauthRequestForm = new OauthRequestForm("", oauthTokenEntity.getRefreshToken(), 3600, "user_read",
                "bearer", clientId, clientSecret, "refresh_token");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        HttpEntity<OauthRequestForm> entity = new HttpEntity<>(oauthRequestForm, headers);

        RestTemplate rt = new RestTemplate();
        final String twitchOauthTokenUrl = "https://id.twitch.tv/oauth2/token";

        ResponseEntity<OauthTokenResponse> response = rt.exchange(
                twitchOauthTokenUrl,
                HttpMethod.POST,
                entity,
                OauthTokenResponse.class);

        OauthTokenResponse receivedOauthTokenVo = response.getBody();
        assert receivedOauthTokenVo != null;
        OauthTokenEntity resultOauthToken = OauthTokenEntity.builder()
                .accessToken(receivedOauthTokenVo.getAccessToken())
                .refreshToken(receivedOauthTokenVo.getRefreshToken())
                .expire(receivedOauthTokenVo.getExpire())
                .createDate(LocalDateTime.now())
                .build();
        insertOauthToken(resultOauthToken);

        return resultOauthToken;
    }

    public AppTokenResponse getAppTokenFromTwitch() {
        RestTemplate restTemplate = new RestTemplate();

        final String tokenUrl = "https://id.twitch.tv/oauth2/token";

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
        parameters.add("client_id", clientId);
        parameters.add("client_secret", clientSecret);
        parameters.add("grant_type", "client_credentials");

        ResponseEntity<AppTokenResponse> responseEntity = restTemplate.postForEntity(tokenUrl, parameters, AppTokenResponse.class);
        log.info("AppTokenResponse: " + responseEntity.getBody());
        return responseEntity.getBody();
    }
}
