package me.cocoblue.twitchwebhook.mapper;

import me.cocoblue.twitchwebhook.dto.Log;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LogMapper {
    int insertLog(Log log);
    Log getLogByIdFromTwitch(String idFromTwitch);
}
