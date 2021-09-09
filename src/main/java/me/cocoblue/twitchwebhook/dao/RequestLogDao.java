package me.cocoblue.twitchwebhook.dao;

import me.cocoblue.twitchwebhook.dto.RequestLog;
import me.cocoblue.twitchwebhook.mapper.RequestLogMapper;
import org.springframework.stereotype.Repository;

@Repository
public class RequestLogDao {
    private final RequestLogMapper requestLogMapper;

    public RequestLogDao(RequestLogMapper requestLogMapper) {
        this.requestLogMapper = requestLogMapper;
    }

    public int insertStreamNotifyRequestLog(RequestLog requestLog) {
        return requestLogMapper.insertStreamNotifyRequestLog(requestLog);
    }
}
