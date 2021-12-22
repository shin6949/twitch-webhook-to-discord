package me.cocoblue.twitchwebhook.dto.twitch.eventsub;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
            this.startedAt = LocalDateTime.parse(startedAtString, formatter);
        }

        public NotificationLogEntity toNotificationLogEntity() {
            final BroadcasterIdEntity broadcasterIdEntity = BroadcasterIdEntity.builder()
                    .id(Long.parseLong(getBroadcasterUserId()))
                    .build();

            return NotificationLogEntity.builder()
                    .idFromTwitch(getId())
                    .broadcasterIdEntity(broadcasterIdEntity)
                    .generatedAt(getStartedAt().plusHours(9))
                    .build();
        }

    }
}
