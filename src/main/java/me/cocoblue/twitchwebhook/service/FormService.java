package me.cocoblue.twitchwebhook.service;

import me.cocoblue.twitchwebhook.dto.Form;

import java.util.List;

public interface FormService {
    List<Form> getStartFormByBroadcasterIdAndType(int broadcasterId, int type);

    List<Form> getEndFormByBroadcasterIdAndType(int broadcasterId, int type);

    List<Integer> getAllBroadcasterId();
}
