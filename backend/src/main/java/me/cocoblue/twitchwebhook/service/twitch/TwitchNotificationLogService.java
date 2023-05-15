package me.cocoblue.twitchwebhook.service.twitch;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.domain.twitch.NotificationLogEntity;
import me.cocoblue.twitchwebhook.domain.twitch.NotificationLogRepository;
import me.cocoblue.twitchwebhook.dto.CommonEvent;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Objects;

@Service
@Log4j2
@AllArgsConstructor
public class TwitchNotificationLogService {
    private final NotificationLogRepository notificationLogRepository;

    public Boolean isAlreadySend(String idFromTwitch) {
        return getLogByIdFromTwitch(idFromTwitch) != null;
    }

    @Transactional
    public NotificationLogEntity insertLog(final CommonEvent event, final HttpHeaders headers) {
        log.debug("event: " + event);
        final String messageId = Objects.requireNonNull(headers.get("twitch-eventsub-message-id")).get(0);
        event.setNotificationIdFromTwitch(messageId);

        return notificationLogRepository.save(event.toNotificationLogEntity());
    }

    private NotificationLogEntity getLogByIdFromTwitch(String idFromTwitch) {
        return notificationLogRepository.getStreamNotifyLogByIdFromTwitch(idFromTwitch);
    }
}
