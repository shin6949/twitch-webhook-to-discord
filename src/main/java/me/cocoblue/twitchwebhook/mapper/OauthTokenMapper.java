package me.cocoblue.twitchwebhook.mapper;

import me.cocoblue.twitchwebhook.dto.OauthToken;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OauthTokenMapper {
    OauthToken getRecentOauthToken();
    int insertOauthToken(OauthToken oauthToken);
}
