package me.cocoblue.twitchwebhook.service;

import me.cocoblue.twitchwebhook.dto.NotifyLog;

public interface NotifyLogService {
    int insertLog(NotifyLog streamNotifyNotifyLog);

    Boolean isAlreadySend(String idFromTwitch);
}
