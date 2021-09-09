package me.cocoblue.twitchwebhook.vo.twitch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Repository
@ToString
public class OauthToken {
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("expires_in")
    private int expire;
    @JsonProperty("refresh_token")
    private String refreshToken;
    @JsonProperty("scope")
    private ArrayList<String> scope;
    @JsonProperty("token_type")
    private String tokenType;
}
