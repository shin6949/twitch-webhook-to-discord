package me.cocoblue.twitchwebhook.dto.twitch.eventsub;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.cocoblue.twitchwebhook.data.TwitchSubscriptionType;
import me.cocoblue.twitchwebhook.domain.twitch.BroadcasterIdEntity;
import me.cocoblue.twitchwebhook.domain.twitch.NotificationLogEntity;

import java.time.LocalDateTime;

public abstract class EventNotificationRequestBody {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Body {
        @JsonProperty("challenge")
        private String challenge;
        @JsonProperty("subscription")
        private Subscription subscription;
        @JsonProperty("event")
        private NotificationEvent event;

        public NotificationLogEntity toNotificationLogEntity() {
            final TwitchSubscriptionType twitchSubscriptionType = TwitchSubscriptionType.find(getSubscription().getType());
            final BroadcasterIdEntity broadcasterIdEntity = BroadcasterIdEntity.builder()
                    .id(Long.valueOf(getEvent().getBroadcasterUserId()))
                    .build();

            return NotificationLogEntity.builder()
                    .twitchSubscriptionType(twitchSubscriptionType)
                    .receivedTime(LocalDateTime.now())
                    .broadcasterIdEntity(broadcasterIdEntity)
                    .build();
        }
    }
}
