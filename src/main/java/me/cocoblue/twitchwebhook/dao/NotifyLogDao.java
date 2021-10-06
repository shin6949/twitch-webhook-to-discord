package me.cocoblue.twitchwebhook.dao;

import lombok.RequiredArgsConstructor;
import me.cocoblue.twitchwebhook.dto.NotifyLog;
import me.cocoblue.twitchwebhook.mapper.NotifyLogMapper;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotifyLogDao {
    private final NotifyLogMapper notifyLogMapper;

    public void insertStreamNotifyLog(NotifyLog notifyLog) {
        notifyLogMapper.insertLog(notifyLog);
    }

    public NotifyLog getLogByIdFromTwitch(String idFromTwitch) {
        return notifyLogMapper.getLogByIdFromTwitch(idFromTwitch);
    }
}
