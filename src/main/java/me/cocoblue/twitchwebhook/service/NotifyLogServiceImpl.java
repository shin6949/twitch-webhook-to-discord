package me.cocoblue.twitchwebhook.service;

import lombok.AllArgsConstructor;
import me.cocoblue.twitchwebhook.dto.twitch.Channel;
import me.cocoblue.twitchwebhook.dto.twitch.eventsub.StreamNotifyRequest;
import me.cocoblue.twitchwebhook.entity.BroadcasterIdEntity;
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

    @Async
    public void insertLog(StreamNotifyRequest.Event event, Channel channel) {
        final BroadcasterIdEntity broadcasterIdEntity = BroadcasterIdEntity.builder()
                .id(Long.parseLong(event.getBroadcasterUserId()))
                .build();

        final NotificationLogEntity notificationLogEntity = NotificationLogEntity.builder()
                .idFromTwitch(event.getId())
                .broadcasterIdEntity(broadcasterIdEntity)
                .generatedAt(event.getStartedAt().plusHours(9))
                .build();

//        notifyLogService.insertLog(notificationLogEntity);
    }

    private NotificationLogEntity getLogByIdFromTwitch(String idFromTwitch) {
        return notificationLogRepository.getStreamNotifyLogByIdFromTwitch(idFromTwitch);
    }
}
