package me.cocoblue.twitchwebhook.service;

import lombok.AllArgsConstructor;
import me.cocoblue.twitchwebhook.entity.BroadcasterIdEntity;
import me.cocoblue.twitchwebhook.entity.SubscriptionFormEntity;
import me.cocoblue.twitchwebhook.repository.SubscriptionFormRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class FormServiceImpl implements FormService {
    private final SubscriptionFormRepository subscriptionFormRepository;

    @Override
    public List<SubscriptionFormEntity> getFormByBroadcasterIdAndType(Long broadcasterId, String type) {

        return subscriptionFormRepository.getStreamNotifyFormsByBroadcasterIdEntityAndType(
                        BroadcasterIdEntity.builder().id(broadcasterId).build(), type);
    }

    @Override
    public List<SubscriptionFormEntity> getFormAll() {
        return subscriptionFormRepository.findAll();
    }
}
