package me.cocoblue.twitchwebhook.service;

import lombok.AllArgsConstructor;
import me.cocoblue.twitchwebhook.entity.StreamNotifyLog;
import me.cocoblue.twitchwebhook.repository.StreamNotifyLogRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class NotifyLogServiceImpl implements NotifyLogService {
    private final StreamNotifyLogRepository streamNotifyLogRepository;

    @Override
    @Async
    public void insertLog(StreamNotifyLog streamNotifyLog) {
        streamNotifyLogRepository.save(streamNotifyLog);
    }

    @Override
    public Boolean isAlreadySend(String idFromTwitch) {
        return getLogByIdFromTwitch(idFromTwitch) != null;
    }

    private StreamNotifyLog getLogByIdFromTwitch(String idFromTwitch) {
        return streamNotifyLogRepository.getStreamNotifyLogByIdFromTwitch(idFromTwitch);
    }
}
