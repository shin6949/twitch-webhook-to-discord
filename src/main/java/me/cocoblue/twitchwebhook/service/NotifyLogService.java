package me.cocoblue.twitchwebhook.service;

import me.cocoblue.twitchwebhook.entity.NotificationLogEntity;

public interface NotifyLogService {
    void insertLog(NotificationLogEntity streamNotifyNotifyLog);
    Boolean isAlreadySend(String idFromTwitch);
}
