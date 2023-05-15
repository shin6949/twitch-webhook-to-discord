package me.cocoblue.twitchwebhook.service.twitch;

import lombok.AllArgsConstructor;
import me.cocoblue.twitchwebhook.data.TwitchSubscriptionType;
import me.cocoblue.twitchwebhook.domain.twitch.BroadcasterIdEntity;
import me.cocoblue.twitchwebhook.domain.discord.SubscriptionFormEntity;
import me.cocoblue.twitchwebhook.domain.discord.SubscriptionFormRepository;
import me.cocoblue.twitchwebhook.domain.push.PushSubscriptionFormEntity;
import me.cocoblue.twitchwebhook.domain.push.PushSubscriptionFormRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class NotificationFormService {
    private final SubscriptionFormRepository subscriptionFormRepository;
    private final PushSubscriptionFormRepository pushSubscriptionFormRepository;

    @Transactional(readOnly=true)
    public List<SubscriptionFormEntity> getFormByBroadcasterIdAndType(Long broadcasterId, String type) {
        final BroadcasterIdEntity broadcasterIdEntity = BroadcasterIdEntity.builder().id(broadcasterId).build();
        final TwitchSubscriptionType twitchSubscriptionType = TwitchSubscriptionType.find(type);

        return subscriptionFormRepository.getStreamNotifyFormsByBroadcasterIdEntityAndTwitchSubscriptionType(
                broadcasterIdEntity, twitchSubscriptionType);
    }

    @Transactional(readOnly=true)
    public List<PushSubscriptionFormEntity> getPushFormByBroadcasterIdAndType(Long broadcasterId, String type) {
        final BroadcasterIdEntity broadcasterIdEntity = BroadcasterIdEntity.builder().id(broadcasterId).build();
        final TwitchSubscriptionType twitchSubscriptionType = TwitchSubscriptionType.find(type);

        return pushSubscriptionFormRepository.getPushSubscriptionFormEntitiesByBroadcasterIdEntityAndTwitchSubscriptionType(
                broadcasterIdEntity, twitchSubscriptionType);
    }
}
