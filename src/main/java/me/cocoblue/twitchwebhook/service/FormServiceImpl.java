package me.cocoblue.twitchwebhook.service;

import lombok.AllArgsConstructor;
import me.cocoblue.twitchwebhook.entity.BroadcasterIdEntity;
import me.cocoblue.twitchwebhook.entity.SubscriptionFormEntity;
import me.cocoblue.twitchwebhook.entity.SubscriptionTypeEntity;
import me.cocoblue.twitchwebhook.repository.SubscriptionFormRepository;
import me.cocoblue.twitchwebhook.repository.SubscriptionTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class FormServiceImpl implements FormService {
    private final SubscriptionFormRepository subscriptionFormRepository;
    private final SubscriptionTypeRepository subscriptionTypeRepository;

    @Override
    public List<SubscriptionFormEntity> getFormByBroadcasterIdAndType(Long broadcasterId, String type) {

        final SubscriptionTypeEntity subscriptionTypeEntity = subscriptionTypeRepository.getSubscriptionTypeEntityByNameEquals(type);
        return subscriptionFormRepository.getStreamNotifyFormsByBroadcasterIdEntityAndSubscriptionTypeEntity(
                        BroadcasterIdEntity.builder().id(broadcasterId).build(), subscriptionTypeEntity);
    }

    @Override
    public List<SubscriptionFormEntity> getFormAll() {
        return subscriptionFormRepository.findAll();
    }
}
