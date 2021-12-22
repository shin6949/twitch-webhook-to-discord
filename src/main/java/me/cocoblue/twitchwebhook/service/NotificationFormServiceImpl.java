package me.cocoblue.twitchwebhook.service;

import lombok.AllArgsConstructor;
import me.cocoblue.twitchwebhook.dto.SubscriptionType;
import me.cocoblue.twitchwebhook.entity.BroadcasterIdEntity;
import me.cocoblue.twitchwebhook.entity.SubscriptionFormEntity;
import me.cocoblue.twitchwebhook.repository.SubscriptionFormRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class NotificationFormServiceImpl implements NotificationFormService {
    private final SubscriptionFormRepository subscriptionFormRepository;

    @Override
    public List<SubscriptionFormEntity> getFormByBroadcasterIdAndType(Long broadcasterId, String type) {
        final BroadcasterIdEntity broadcasterIdEntity = BroadcasterIdEntity.builder().id(broadcasterId).build();
        final SubscriptionType subscriptionType = SubscriptionType.find(type);

        return subscriptionFormRepository.getStreamNotifyFormsByBroadcasterIdEntityAndSubscriptionType(
                broadcasterIdEntity, subscriptionType);
    }

    @Override
    public List<SubscriptionFormEntity> getFormAll() {
        return subscriptionFormRepository.findAll();
    }
}
