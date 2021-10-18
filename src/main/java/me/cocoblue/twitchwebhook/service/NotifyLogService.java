package me.cocoblue.twitchwebhook.service;

import me.cocoblue.twitchwebhook.entity.StreamNotifyLog;

public interface NotifyLogService {
    void insertLog(StreamNotifyLog streamNotifyNotifyLog);
    Boolean isAlreadySend(String idFromTwitch);
}
