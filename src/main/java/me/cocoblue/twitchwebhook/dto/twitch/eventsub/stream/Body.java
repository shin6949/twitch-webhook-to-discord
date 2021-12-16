package me.cocoblue.twitchwebhook.dto.twitch.eventsub.stream;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.cocoblue.twitchwebhook.dto.twitch.eventsub.Subscription;
import me.cocoblue.twitchwebhook.dto.twitch.eventsub.stream.Event;

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
