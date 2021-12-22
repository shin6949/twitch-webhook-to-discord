package me.cocoblue.twitchwebhook.service;

import me.cocoblue.twitchwebhook.dto.CommonEvent;
import me.cocoblue.twitchwebhook.entity.NotificationLogEntity;

public interface NotifyLogService {
    void insertLog(CommonEvent commonEvent);
    Boolean isAlreadySend(String idFromTwitch);
}
