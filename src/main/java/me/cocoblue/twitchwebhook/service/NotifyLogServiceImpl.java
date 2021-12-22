package me.cocoblue.twitchwebhook.service;

import lombok.AllArgsConstructor;
import me.cocoblue.twitchwebhook.dto.CommonEvent;
import me.cocoblue.twitchwebhook.entity.NotificationLogEntity;
import me.cocoblue.twitchwebhook.repository.NotificationLogRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class NotifyLogServiceImpl implements NotifyLogService {
    private final NotificationLogRepository notificationLogRepository;

    @Override
    public Boolean isAlreadySend(String idFromTwitch) {
        return getLogByIdFromTwitch(idFromTwitch) != null;
    }

    @Async
    @Override
    public void insertLog(CommonEvent event) {
        notificationLogRepository.save(event.toNotificationLogEntity());
    }

    private NotificationLogEntity getLogByIdFromTwitch(String idFromTwitch) {
        return notificationLogRepository.getStreamNotifyLogByIdFromTwitch(idFromTwitch);
    }
}
