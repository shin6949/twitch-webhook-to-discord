package me.cocoblue.twitchwebhook.dto.twitch.eventsub.channel.update;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.cocoblue.twitchwebhook.dto.twitch.eventsub.Subscription;

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
