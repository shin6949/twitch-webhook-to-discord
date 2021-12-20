package me.cocoblue.twitchwebhook.service;

import lombok.AllArgsConstructor;
import me.cocoblue.twitchwebhook.entity.NotificationLogEntity;
import me.cocoblue.twitchwebhook.repository.NotificationLogRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class NotifyLogServiceImpl implements NotifyLogService {
    private final NotificationLogRepository notificationLogRepository;

    @Override
    @Async
    public void insertLog(NotificationLogEntity notificationLogEntity) {
        notificationLogRepository.save(notificationLogEntity);
    }

    @Override
    public Boolean isAlreadySend(String idFromTwitch) {
        return getLogByIdFromTwitch(idFromTwitch) != null;
    }

    private NotificationLogEntity getLogByIdFromTwitch(String idFromTwitch) {
        return notificationLogRepository.getStreamNotifyLogByIdFromTwitch(idFromTwitch);
    }
}
