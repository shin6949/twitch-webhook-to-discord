package me.cocoblue.twitchwebhook.service;

import me.cocoblue.twitchwebhook.dto.twitch.AppTokenResponse;

public interface OauthTokenService {
    AppTokenResponse getAppTokenFromTwitch();
    void revokeAppTokenToTwitch(String appAccessToken);
}
