package me.cocoblue.twitchwebhook.mapper;

import me.cocoblue.twitchwebhook.dto.GameIndex;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GameIndexMapper {
    int insertGameIndex(GameIndex gameIndex);
}
