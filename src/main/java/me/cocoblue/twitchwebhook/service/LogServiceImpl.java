package me.cocoblue.twitchwebhook.service;

import lombok.AllArgsConstructor;
import me.cocoblue.twitchwebhook.dao.LogDao;
import me.cocoblue.twitchwebhook.dto.Log;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LogServiceImpl implements LogService {
    private final LogDao logDao;

    @Override
    public int insertLog(Log log) {
        return logDao.insertStreamNotifyLog(log);
    }

    private Log getLogByIdFromTwitch(String idFromTwitch) {
        return logDao.getLogByIdFromTwitch(idFromTwitch);
    }

    @Override
    public Boolean isAlreadySend(String idFromTwitch) {
        return getLogByIdFromTwitch(idFromTwitch) != null;
    }
}
