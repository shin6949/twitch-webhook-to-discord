package me.cocoblue.twitchwebhook.vo.twitch.eventsub;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Header {
    private final String messageJsonPrefix = "Twitch-Eventsub-Message-";
    private final String subscriptionJsonPrefix = "Twitch-Eventsub-Subscription-";

    @JsonProperty(messageJsonPrefix + "Id")
    private String id;
    @JsonProperty(messageJsonPrefix + "Retry")
    private int retry;
    @JsonProperty(messageJsonPrefix + "Type")
    private String messageType;
    @JsonProperty(messageJsonPrefix + "Signature")
    private String signature;
    @JsonProperty(messageJsonPrefix + "Timestamp")
    private LocalDateTime timestamp;
    @JsonProperty(subscriptionJsonPrefix + "Type")
    private String subscriptionType;
    @JsonProperty(subscriptionJsonPrefix + "Version")
    private float version;
}
