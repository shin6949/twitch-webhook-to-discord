package me.cocoblue.twitchwebhook.service;

import me.cocoblue.twitchwebhook.dao.RequestLogDao;
import me.cocoblue.twitchwebhook.dto.RequestLog;
import org.springframework.stereotype.Service;

@Service
public class RequestLogServiceImpl implements RequestLogService {
    private final RequestLogDao requestLogDao;

    public RequestLogServiceImpl(RequestLogDao requestLogDao) {
        this.requestLogDao = requestLogDao;
    }

    @Override
    public int insertStreamNotifyRequestLog(RequestLog requestLog) {
        return requestLogDao.insertStreamNotifyRequestLog(requestLog);
    }
}
