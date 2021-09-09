package me.cocoblue.twitchwebhook.service;

import me.cocoblue.twitchwebhook.dao.OauthTokenDao;
import me.cocoblue.twitchwebhook.dto.OauthRequestForm;
import me.cocoblue.twitchwebhook.vo.twitch.OauthToken;
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
    public me.cocoblue.twitchwebhook.dto.OauthToken getRecentOauthToken() {
        return oauthTokenDao.getRecentOauthToken();
    }

    @Override
    public int insertOauthToken(me.cocoblue.twitchwebhook.dto.OauthToken oauthToken) {
        return oauthTokenDao.insertOauthToken(oauthToken);
    }

    @Override
    public me.cocoblue.twitchwebhook.dto.OauthToken getOauthTokenFromTwitch() {
        me.cocoblue.twitchwebhook.dto.OauthToken oauthToken = getRecentOauthToken();
        OauthRequestForm oauthRequestForm = new OauthRequestForm("", oauthToken.getRefreshToken(), 3600, "user_read",
                "bearer", clientId, clientSecret, "refresh_token");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        HttpEntity<OauthRequestForm> entity = new HttpEntity<>(oauthRequestForm, headers);

        RestTemplate rt = new RestTemplate();
        ResponseEntity<OauthToken> response = rt.exchange(
                "https://id.twitch.tv/oauth2/token", //{요청할 서버 주소}
                HttpMethod.POST, //{요청할 방식}
                entity, // {요청할 때 보낼 데이터}
                OauthToken.class);

        OauthToken receivedOauthToken = response.getBody();
        me.cocoblue.twitchwebhook.dto.OauthToken resultOauthToken = new me.cocoblue.twitchwebhook.dto.OauthToken(0, receivedOauthToken.getAccessToken(),
                receivedOauthToken.getRefreshToken(), receivedOauthToken.getExpire(), LocalDateTime.now());

        oauthTokenDao.insertOauthToken(resultOauthToken);

        return resultOauthToken;
    }
}
