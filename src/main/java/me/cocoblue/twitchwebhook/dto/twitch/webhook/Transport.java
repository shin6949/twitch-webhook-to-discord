package me.cocoblue.twitchwebhook.dto.twitch.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transport {
    @JsonProperty("method")
    private String method;
    @JsonProperty("callback")
    private String callback;
    @JsonProperty("secret")
    private String secret;

    public Transport(String callback, String secret) {
        this.method = "webhook";
        this.callback = callback;
        this.secret = secret;
    }
}
