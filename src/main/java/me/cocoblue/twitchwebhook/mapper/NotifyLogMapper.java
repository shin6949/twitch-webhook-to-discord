package me.cocoblue.twitchwebhook.mapper;

import me.cocoblue.twitchwebhook.dto.NotifyLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NotifyLogMapper {
    int insertLog(NotifyLog notifyLog);
    NotifyLog getLogByIdFromTwitch(String idFromTwitch);
}
