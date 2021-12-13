package me.cocoblue.twitchwebhook.service;

import me.cocoblue.twitchwebhook.dto.twitch.AppTokenResponse;
import me.cocoblue.twitchwebhook.entity.OauthTokenEntity;

public interface OauthTokenService {
    OauthTokenEntity getRecentOauthToken();
    void insertOauthToken(OauthTokenEntity oauthTokenEntity);
    OauthTokenEntity getOauthTokenFromTwitch();
    AppTokenResponse getAppTokenFromTwitch();
}
