package me.cocoblue.twitchwebhook.dao;

import me.cocoblue.twitchwebhook.dto.NotifyLog;
import me.cocoblue.twitchwebhook.mapper.NotifyLogMapper;
import org.springframework.stereotype.Repository;

@Repository
public class NotifyLogDao {
    private final NotifyLogMapper notifyLogMapper;

    public NotifyLogDao(NotifyLogMapper notifyLogMapper) {
        this.notifyLogMapper = notifyLogMapper;
    }

    public int insertStreamNotifyLog(NotifyLog notifyLog) {
        return notifyLogMapper.insertLog(notifyLog);
    }

    public NotifyLog getLogByIdFromTwitch(String idFromTwitch) {
        return notifyLogMapper.getLogByIdFromTwitch(idFromTwitch);
    }
}
