package me.cocoblue.twitchwebhook.service;

import me.cocoblue.twitchwebhook.entity.StreamNotifyLogEntity;

public interface NotifyLogService {
    void insertLog(StreamNotifyLogEntity streamNotifyNotifyLog);
    Boolean isAlreadySend(String idFromTwitch);
}
