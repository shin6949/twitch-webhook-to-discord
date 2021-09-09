package me.cocoblue.twitchwebhook.dao;

import me.cocoblue.twitchwebhook.dto.Log;
import me.cocoblue.twitchwebhook.mapper.LogMapper;
import org.springframework.stereotype.Repository;

@Repository
public class LogDao {
    private final LogMapper logMapper;

    public LogDao(LogMapper logMapper) {
        this.logMapper = logMapper;
    }

    public int insertStreamNotifyLog(Log log) {
        return logMapper.insertLog(log);
    }

    public Log getLogByIdFromTwitch(String idFromTwitch) {
        return logMapper.getLogByIdFromTwitch(idFromTwitch);
    }
}
