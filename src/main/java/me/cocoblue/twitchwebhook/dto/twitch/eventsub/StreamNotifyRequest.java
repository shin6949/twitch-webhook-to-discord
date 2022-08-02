package me.cocoblue.twitchwebhook.dto.twitch.eventsub;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.cocoblue.twitchwebhook.dto.CommonEvent;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

public class StreamNotifyRequest {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Body {
        @JsonProperty("challenge")
        private String challenge;
        @JsonProperty("subscription")
        private Subscription subscription;
        @JsonProperty("event")
        private Event event;

        public CommonEvent toCommonEvent() {
            final StreamNotifyRequest.Event event = getEvent();

            return CommonEvent.builder()
                    .notificationIdFromTwitch(event.getId())
                    .subscriptionType(subscription.getType())
                    .broadcasterId(Long.parseLong(event.getBroadcasterUserId()))
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Event {
        @JsonProperty("id")
        private String id;
        @JsonProperty("broadcaster_user_id")
        private String broadcasterUserId;
        @JsonProperty("broadcaster_user_login")
        private String broadcasterUserLogin;
        @JsonProperty("broadcaster_user_name")
        private String broadcasterUserName;
        @JsonProperty("type")
        private String type;
        @JsonProperty("started_at")
        private LocalDateTime startedAt;

        public void setStartedAt(String startedAtString) {
            // TEST Case에는 소수점이 있으므로 삭제.
            final int dotIndex = startedAtString.indexOf('.');
            String cutString;
            String timePattern;

            if(dotIndex > 0) {
                cutString = startedAtString.substring(0, dotIndex);
                timePattern = "yyyy-MM-dd'T'HH:mm:ss";
            } else {
                cutString = startedAtString;
                timePattern = "yyyy-MM-dd'T'HH:mm:ss'Z'";
            }

            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(timePattern)
                    .withResolverStyle(ResolverStyle.LENIENT);

            this.startedAt = LocalDateTime.parse(cutString, formatter);
        }

        public void setStartedAt(LocalDateTime startedAt) {
            this.startedAt = startedAt;
        }
    }
}
