package me.cocoblue.twitchwebhook.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.dto.CommonEvent;
import me.cocoblue.twitchwebhook.domain.NotificationLogEntity;
import me.cocoblue.twitchwebhook.domain.NotificationLogRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@AllArgsConstructor
public class NotifyLogService {
    private final NotificationLogRepository notificationLogRepository;

    public Boolean isAlreadySend(String idFromTwitch) {
        return getLogByIdFromTwitch(idFromTwitch) != null;
    }

    @Async
    public void insertLog(CommonEvent event, HttpHeaders headers) {
        log.debug("event: " + event);
        final String messageId = headers.get("twitch-eventsub-message-id").get(0);
        event.setNotificationIdFromTwitch(messageId);

        notificationLogRepository.save(event.toNotificationLogEntity());
    }

    private NotificationLogEntity getLogByIdFromTwitch(String idFromTwitch) {
        return notificationLogRepository.getStreamNotifyLogByIdFromTwitch(idFromTwitch);
    }
}
