package me.cocoblue.twitchwebhook.service;

import me.cocoblue.twitchwebhook.dto.Log;

public interface LogService {
    int insertLog(Log streamNotifyLog);
    Boolean isAlreadySend(String idFromTwitch);
}
