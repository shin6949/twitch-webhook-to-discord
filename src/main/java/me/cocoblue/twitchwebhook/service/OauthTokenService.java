package me.cocoblue.twitchwebhook.service;

import me.cocoblue.twitchwebhook.dto.OauthToken;

public interface OauthTokenService {
    OauthToken getRecentOauthToken();
    int insertOauthToken(OauthToken oauthToken);
    OauthToken getOauthTokenFromTwitch();
}
