package me.cocoblue.twitchwebhook.dao;

import me.cocoblue.twitchwebhook.dto.GameIndex;
import me.cocoblue.twitchwebhook.mapper.GameIndexMapper;
import org.springframework.stereotype.Repository;

@Repository
public class GameIndexDao {
    private final GameIndexMapper gameIndexMapper;

    public GameIndexDao(GameIndexMapper gameIndexMapper) {
        this.gameIndexMapper = gameIndexMapper;
    }

    public int insertGameIndex(GameIndex gameIndex) {
        return gameIndexMapper.insertGameIndex(gameIndex);
    }
}
