package me.cocoblue.twitchwebhook.dto.twitch.eventsub;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

public class ChannelUpdateRequestBody extends EventNotificationRequestBody {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static class Body extends EventNotificationRequestBody.Body {
        @JsonProperty("event")
        private ChannelUpdateRequestEvent event;

        @Override
        public ChannelUpdateRequestEvent getEvent() {
            return event;
        }
    }
}