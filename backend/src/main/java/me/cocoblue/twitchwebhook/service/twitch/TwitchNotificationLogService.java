package me.cocoblue.twitchwebhook.service.twitch;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.domain.twitch.NotificationLogEntity;
import me.cocoblue.twitchwebhook.domain.twitch.NotificationLogRepository;
import me.cocoblue.twitchwebhook.dto.twitch.eventsub.EventNotificationRequestBody;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Objects;

@Service
@Log4j2
@AllArgsConstructor
public class TwitchNotificationLogService {
    private final NotificationLogRepository notificationLogRepository;

    public Boolean isAlreadySend(final String idFromTwitch) {
        return getLogByIdFromTwitch(idFromTwitch) != null;
    }

    @Transactional
    public <T extends EventNotificationRequestBody.Body> NotificationLogEntity insertLog(final T eventBody, final HttpHeaders headers) {
        log.debug("eventBody: " + eventBody);
        final String messageId = Objects.requireNonNull(headers.get("twitch-eventsub-message-id")).get(0);
        final NotificationLogEntity notificationLogEntity = eventBody.toNotificationLogEntity();

        notificationLogEntity.setIdFromTwitch(messageId);

        return notificationLogRepository.save(notificationLogEntity);
    }

    private NotificationLogEntity getLogByIdFromTwitch(String idFromTwitch) {
        return notificationLogRepository.getStreamNotifyLogByIdFromTwitch(idFromTwitch);
    }
}
