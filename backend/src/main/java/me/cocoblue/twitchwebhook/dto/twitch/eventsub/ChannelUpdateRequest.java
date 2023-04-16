package me.cocoblue.twitchwebhook.dto.twitch.eventsub;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.cocoblue.twitchwebhook.dto.CommonEvent;

public class ChannelUpdateRequest {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Body {
        @JsonProperty("challenge")
        private String challenge;
        @JsonProperty("subscription")
        private Subscription subscription;
        @JsonProperty("event")
        private Event event;

        public CommonEvent toCommonEvent() {
            final ChannelUpdateRequest.Event event = getEvent();

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
        @JsonProperty("title")
        private String title;
        @JsonProperty("language")
        private String language;
        @JsonProperty("category_id")
        private String categoryId;
        @JsonProperty("category_name")
        private String categoryName;
        @JsonProperty("is_mature")
        private boolean isMature;
    }
}
