package me.cocoblue.twitchwebhook.service;

import lombok.AllArgsConstructor;
import me.cocoblue.twitchwebhook.entity.StreamNotifyLogEntity;
import me.cocoblue.twitchwebhook.repository.StreamNotifyLogRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class NotifyLogServiceImpl implements NotifyLogService {
    private final StreamNotifyLogRepository streamNotifyLogRepository;

    @Override
    @Async
    public void insertLog(StreamNotifyLogEntity streamNotifyLogEntity) {
        streamNotifyLogRepository.save(streamNotifyLogEntity);
    }

    @Override
    public Boolean isAlreadySend(String idFromTwitch) {
        return getLogByIdFromTwitch(idFromTwitch) != null;
    }

    private StreamNotifyLogEntity getLogByIdFromTwitch(String idFromTwitch) {
        return streamNotifyLogRepository.getStreamNotifyLogByIdFromTwitch(idFromTwitch);
    }
}
