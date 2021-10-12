package me.cocoblue.twitchwebhook.service;

import me.cocoblue.twitchwebhook.dto.Form;

import java.util.List;

public interface FormService {
    List<Form> getFormByBroadcasterIdAndType(int broadcasterId, String type);
    List<Form> getFormAll();
}
