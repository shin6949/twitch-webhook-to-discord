package me.cocoblue.twitchwebhook.service;

import lombok.RequiredArgsConstructor;
import me.cocoblue.twitchwebhook.dto.twitch.OauthRequestForm;
import me.cocoblue.twitchwebhook.entity.OauthTokenEntity;
import me.cocoblue.twitchwebhook.repository.OauthTokenRepository;
import me.cocoblue.twitchwebhook.vo.twitch.OauthTokenVo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

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
        String twitchOauthTokenUrl = "https://id.twitch.tv/oauth2/token";

        ResponseEntity<OauthTokenVo> response = rt.exchange(
                twitchOauthTokenUrl, //{요청할 서버 주소}
                HttpMethod.POST, //{요청할 방식}
                entity, // {요청할 때 보낼 데이터}
                OauthTokenVo.class);

        OauthTokenVo receivedOauthTokenVo = response.getBody();
        assert receivedOauthTokenVo != null;
        OauthTokenEntity resultOauthToken = OauthTokenEntity.builder()
                .accessToken(receivedOauthTokenVo.getAccessToken())
                .refreshToken(receivedOauthTokenVo.getRefreshToken())
                .expire(receivedOauthTokenVo.getExpire())
                .createDate(LocalDateTime.now())
                .build();

        oauthTokenRepository.save(resultOauthToken);

        return resultOauthToken;
    }
}
