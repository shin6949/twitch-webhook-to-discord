package me.cocoblue.twitchwebhook.vo.twitch.eventsub;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Body {
    @JsonProperty("challenge")
    private String challenge;
    @JsonProperty("subscription")
    private Subscription subscription;
    @JsonProperty("event")
    private Event event;
}
