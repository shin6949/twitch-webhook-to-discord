package me.cocoblue.twitchwebhook.service;

import me.cocoblue.twitchwebhook.dto.CommonEvent;
import org.springframework.http.HttpHeaders;

public interface NotifyLogService {
    void insertLog(CommonEvent event, HttpHeaders headers);
    Boolean isAlreadySend(String idFromTwitch);
}
