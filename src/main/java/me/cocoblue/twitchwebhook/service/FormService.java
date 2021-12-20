package me.cocoblue.twitchwebhook.service;

import me.cocoblue.twitchwebhook.entity.SubscriptionFormEntity;

import java.util.List;

public interface FormService {
    List<SubscriptionFormEntity> getFormByBroadcasterIdAndType(Long broadcasterId, String type);
    List<SubscriptionFormEntity> getFormAll();
}
