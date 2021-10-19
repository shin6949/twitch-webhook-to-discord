package me.cocoblue.twitchwebhook.service;

import me.cocoblue.twitchwebhook.entity.StreamNotifyForm;

import java.util.List;

public interface FormService {
    List<StreamNotifyForm> getFormByBroadcasterIdAndType(Long broadcasterId, String type);
    List<StreamNotifyForm> getFormAll();
}
