package me.cocoblue.twitchwebhook.dto.twitch.eventsub;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

public class StreamNotifyRequestBody extends EventNotificationRequestBody {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = true)
    public static class Body extends EventNotificationRequestBody.Body {
        @JsonProperty("event")
        private StreamNotifyRequestEvent event;

        @Override
        public StreamNotifyRequestEvent getEvent() {
            return event;
        }
    }
}
