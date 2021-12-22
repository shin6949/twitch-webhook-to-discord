package me.cocoblue.twitchwebhook.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.cocoblue.twitchwebhook.entity.NotificationLogEntity;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonEvent {
    private String notificationIdFromTwitch;
    private String subscriptionType;
    private long broadcasterId;
    private LocalDateTime generatedAt;

    public NotificationLogEntity toNotificationLogEntity() {
        final SubscriptionType subscriptionType = SubscriptionType.find(getSubscriptionType());

        return NotificationLogEntity.builder()
                .idFromTwitch(notificationIdFromTwitch)
                .subscriptionType(subscriptionType)
                .build();
    }
}
