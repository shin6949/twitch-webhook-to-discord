package me.cocoblue.twitchwebhook.service;

import me.cocoblue.twitchwebhook.dao.OauthTokenDao;
import me.cocoblue.twitchwebhook.dto.twitch.OauthRequestForm;
import me.cocoblue.twitchwebhook.dto.OauthToken;
import me.cocoblue.twitchwebhook.vo.twitch.OauthTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Service
public class OauthTokenServiceImpl implements OauthTokenService {
    private final OauthTokenDao oauthTokenDao;

    public OauthTokenServiceImpl(OauthTokenDao oauthTokenDao) {
        this.oauthTokenDao = oauthTokenDao;
    }

    @Value("${twitch.client-id}")
    private String clientId;

    @Value("${twitch.client-secret}")
    private String clientSecret;

    @Override
    public OauthToken getRecentOauthToken() {
        return oauthTokenDao.getRecentOauthToken();
    }

    @Override
    public int insertOauthToken(OauthToken oauthToken) {
        return oauthTokenDao.insertOauthToken(oauthToken);
    }

    @Override
    public OauthToken getOauthTokenFromTwitch() {
        OauthToken oauthToken = getRecentOauthToken();
        OauthRequestForm oauthRequestForm = new OauthRequestForm("", oauthToken.getRefreshToken(), 3600, "user_read",
                "bearer", clientId, clientSecret, "refresh_token");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        HttpEntity<OauthRequestForm> entity = new HttpEntity<>(oauthRequestForm, headers);

        RestTemplate rt = new RestTemplate();
        String twitchOauthTokenUrl = "https://id.twitch.tv/oauth2/token";

        ResponseEntity<OauthTokenResponse> response = rt.exchange(
                twitchOauthTokenUrl, //{요청할 서버 주소}
                HttpMethod.POST, //{요청할 방식}
                entity, // {요청할 때 보낼 데이터}
                OauthTokenResponse.class);

        OauthTokenResponse receivedOauthTokenResponse = response.getBody();
        assert receivedOauthTokenResponse != null;
        OauthToken resultOauthToken = new OauthToken(0, receivedOauthTokenResponse.getAccessToken(),
                receivedOauthTokenResponse.getRefreshToken(), receivedOauthTokenResponse.getExpire(), LocalDateTime.now());

        oauthTokenDao.insertOauthToken(resultOauthToken);

        return resultOauthToken;
    }
}
