package me.cocoblue.twitchwebhook.service;

import lombok.AllArgsConstructor;
import me.cocoblue.twitchwebhook.dao.RequestLogDao;
import me.cocoblue.twitchwebhook.dto.RequestLog;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RequestLogServiceImpl implements RequestLogService {
    private final RequestLogDao requestLogDao;

    @Override
    @Async
    public void insertStreamNotifyRequestLog(RequestLog requestLog) {
        requestLogDao.insertStreamNotifyRequestLog(requestLog);
    }
}
