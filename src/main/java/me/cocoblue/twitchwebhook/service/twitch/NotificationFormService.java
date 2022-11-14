package me.cocoblue.twitchwebhook.service.twitch;

import lombok.AllArgsConstructor;
import me.cocoblue.twitchwebhook.data.TwitchSubscriptionType;
import me.cocoblue.twitchwebhook.domain.BroadcasterIdEntity;
import me.cocoblue.twitchwebhook.domain.SubscriptionFormEntity;
import me.cocoblue.twitchwebhook.domain.SubscriptionFormRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class NotificationFormService {
    private final SubscriptionFormRepository subscriptionFormRepository;

    @Transactional(readOnly=true)
    public List<SubscriptionFormEntity> getFormByBroadcasterIdAndType(Long broadcasterId, String type) {
        final BroadcasterIdEntity broadcasterIdEntity = BroadcasterIdEntity.builder().id(broadcasterId).build();
        final TwitchSubscriptionType twitchSubscriptionType = TwitchSubscriptionType.find(type);

        return subscriptionFormRepository.getStreamNotifyFormsByBroadcasterIdEntityAndTwitchSubscriptionType(
                broadcasterIdEntity, twitchSubscriptionType);
    }
}
