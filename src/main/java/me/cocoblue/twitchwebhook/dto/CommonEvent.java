package me.cocoblue.twitchwebhook.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.data.TwitchSubscriptionType;
import me.cocoblue.twitchwebhook.domain.BroadcasterIdEntity;
import me.cocoblue.twitchwebhook.domain.NotificationLogEntity;

@Data
@Builder
@Log4j2
@NoArgsConstructor
@AllArgsConstructor
public class CommonEvent {
    private String notificationIdFromTwitch;
    private String subscriptionType;
    private long broadcasterId;

    public NotificationLogEntity toNotificationLogEntity() {
        final TwitchSubscriptionType twitchSubscriptionType = TwitchSubscriptionType.find(getSubscriptionType());
        final BroadcasterIdEntity broadcasterIdEntity = BroadcasterIdEntity.builder().id(broadcasterId).build();

        return NotificationLogEntity.builder()
                .idFromTwitch(notificationIdFromTwitch)
                .twitchSubscriptionType(twitchSubscriptionType)
                .broadcasterIdEntity(broadcasterIdEntity)
                .build();
    }
}
