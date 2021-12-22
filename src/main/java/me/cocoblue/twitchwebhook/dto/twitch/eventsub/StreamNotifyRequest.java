package me.cocoblue.twitchwebhook.dto.twitch.eventsub;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.cocoblue.twitchwebhook.dto.CommonEvent;
import me.cocoblue.twitchwebhook.entity.BroadcasterIdEntity;
import me.cocoblue.twitchwebhook.entity.NotificationLogEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
        private String startedAtString;
        private LocalDateTime startedAt;

        public void setStartedAtString(String startedAtString) {
            this.startedAtString = startedAtString;
            // String Value를 LocalDateTime에도 반영
            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
            this.startedAt = LocalDateTime.parse(startedAtString, formatter);
        }

        public CommonEvent toCommonEvent() {
            return CommonEvent.builder()
                    .notificationIdFromTwitch(id)
                    .subscriptionType(type)
                    .broadcasterId(Long.parseLong(broadcasterUserId))
                    // KST 반영
                    .generatedAt(startedAt.plusHours(9))
                    .build();
        }

    }
}
