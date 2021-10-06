package me.cocoblue.twitchwebhook.service;

import me.cocoblue.twitchwebhook.dto.NotifyLog;

public interface NotifyLogService {
    void insertLog(NotifyLog streamNotifyNotifyLog);
    Boolean isAlreadySend(String idFromTwitch);
}
