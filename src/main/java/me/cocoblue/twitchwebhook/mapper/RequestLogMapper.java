package me.cocoblue.twitchwebhook.mapper;

import me.cocoblue.twitchwebhook.dto.RequestLog;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface RequestLogMapper {
    int insertStreamNotifyRequestLog(RequestLog requestLog);
}
