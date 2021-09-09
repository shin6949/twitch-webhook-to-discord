package me.cocoblue.twitchwebhook.service;

import me.cocoblue.twitchwebhook.dao.OauthTokenDao;
import me.cocoblue.twitchwebhook.dto.OauthRequestForm;
import me.cocoblue.twitchwebhook.dto.OauthToken;
import me.cocoblue.twitchwebhook.vo.TwitchOauthToken;
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

    @Value("${twitch.client-id}")
    private String clientId;

    @Value("${twitch.client-secret}")
    private String clientSecret;

    public OauthTokenServiceImpl(OauthTokenDao oauthTokenDao) {
        this.oauthTokenDao = oauthTokenDao;
    }

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
        HttpEntity<OauthRequestForm> entity = new HttpEntity<OauthRequestForm>(oauthRequestForm, headers);

        RestTemplate rt = new RestTemplate();
        ResponseEntity<TwitchOauthToken> response = rt.exchange(
                "https://id.twitch.tv/oauth2/token", //{요청할 서버 주소}
                HttpMethod.POST, //{요청할 방식}
                entity, // {요청할 때 보낼 데이터}
                TwitchOauthToken.class);

        TwitchOauthToken receivedOauthToken = response.getBody();
        OauthToken resultOauthToken = new OauthToken(0, receivedOauthToken.getAccessToken(),
                receivedOauthToken.getRefreshToken(), receivedOauthToken.getExpire(), LocalDateTime.now());

        oauthTokenDao.insertOauthToken(resultOauthToken);

        return resultOauthToken;
    }
}
