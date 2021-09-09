package me.cocoblue.twitchwebhook.service;


import me.cocoblue.twitchwebhook.dto.RequestLog;

public interface RequestLogService {
    void insertStreamNotifyRequestLog(RequestLog requestLog);
}
