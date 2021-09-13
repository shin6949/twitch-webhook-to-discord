package me.cocoblue.twitchwebhook.dto.twitch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
    private String secret;
}
