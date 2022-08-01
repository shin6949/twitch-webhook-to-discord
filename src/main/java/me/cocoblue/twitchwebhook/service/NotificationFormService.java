package me.cocoblue.twitchwebhook.service;

import lombok.AllArgsConstructor;
import me.cocoblue.twitchwebhook.data.SubscriptionType;
import me.cocoblue.twitchwebhook.entity.BroadcasterIdEntity;
import me.cocoblue.twitchwebhook.entity.SubscriptionFormEntity;
import me.cocoblue.twitchwebhook.repository.SubscriptionFormRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class NotificationFormService {
    private final SubscriptionFormRepository subscriptionFormRepository;

    public List<SubscriptionFormEntity> getFormByBroadcasterIdAndType(Long broadcasterId, String type) {
        final BroadcasterIdEntity broadcasterIdEntity = BroadcasterIdEntity.builder().id(broadcasterId).build();
        final SubscriptionType subscriptionType = SubscriptionType.find(type);

        return subscriptionFormRepository.getStreamNotifyFormsByBroadcasterIdEntityAndSubscriptionType(
                broadcasterIdEntity, subscriptionType);
    }

    public List<SubscriptionFormEntity> getFormAll() {
        return subscriptionFormRepository.findAll();
    }
}
