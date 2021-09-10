package me.cocoblue.twitchwebhook.dto.twitch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Repository
public class WebhookRequestForm {
    @JsonProperty("hub.callback")
    private String callbackUrl;
    @JsonProperty("hub.mode")
    private String mode;
    @JsonProperty("hub.topic")
    private String topic;
    @JsonProperty("hub.lease_seconds")
    private int leaseSeconds;
    @JsonProperty("hub.secret")
    @Value("${twitch.hub.secret}")
    private String secret;

    public WebhookRequestForm(String callbackUrl, String mode, String topic) {
        this.callbackUrl = callbackUrl;
        this.mode = mode;
        this.topic = topic;
        this.leaseSeconds = 3600;
    }
}
