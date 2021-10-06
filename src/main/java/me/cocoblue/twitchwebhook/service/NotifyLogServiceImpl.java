package me.cocoblue.twitchwebhook.service;

import lombok.AllArgsConstructor;
import me.cocoblue.twitchwebhook.dao.NotifyLogDao;
import me.cocoblue.twitchwebhook.dto.NotifyLog;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class NotifyLogServiceImpl implements NotifyLogService {
    private final NotifyLogDao notifyLogDao;

    @Override
    @Async
    public void insertLog(NotifyLog notifyLog) {
        notifyLogDao.insertStreamNotifyLog(notifyLog);
    }

    private NotifyLog getLogByIdFromTwitch(String idFromTwitch) {
        return notifyLogDao.getLogByIdFromTwitch(idFromTwitch);
    }

    @Override
    public Boolean isAlreadySend(String idFromTwitch) {
        return getLogByIdFromTwitch(idFromTwitch) != null;
    }
}
