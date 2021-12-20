package me.cocoblue.twitchwebhook.service;

import me.cocoblue.twitchwebhook.entity.StreamNotifyFormEntity;

import java.util.List;

public interface FormService {
    List<StreamNotifyFormEntity> getFormByBroadcasterIdAndType(Long broadcasterId, String type);
    List<StreamNotifyFormEntity> getFormAll();
}
