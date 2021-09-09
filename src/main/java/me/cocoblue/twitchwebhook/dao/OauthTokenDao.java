package me.cocoblue.twitchwebhook.dao;

import me.cocoblue.twitchwebhook.dto.OauthToken;
import me.cocoblue.twitchwebhook.mapper.OauthTokenMapper;
import org.springframework.stereotype.Repository;

@Repository
public class OauthTokenDao {
    private final OauthTokenMapper oauthTokenMapper;

    public OauthTokenDao(OauthTokenMapper oauthTokenMapper) {
        this.oauthTokenMapper = oauthTokenMapper;
    }

    public OauthToken getRecentOauthToken() {
        return oauthTokenMapper.getRecentOauthToken();
    }

    public int insertOauthToken(OauthToken oauthToken) {
        return oauthTokenMapper.insertOauthToken(oauthToken);
    }
}
