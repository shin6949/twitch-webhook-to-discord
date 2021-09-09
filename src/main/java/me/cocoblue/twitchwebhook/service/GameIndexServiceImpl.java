package me.cocoblue.twitchwebhook.service;

import me.cocoblue.twitchwebhook.dao.GameIndexDao;
import me.cocoblue.twitchwebhook.dto.GameIndex;
import org.springframework.stereotype.Service;

@Service
public class GameIndexServiceImpl implements GameIndexService {
    private final GameIndexDao gameIndexDao;

    public GameIndexServiceImpl(GameIndexDao gameIndexDao) {
        this.gameIndexDao = gameIndexDao;
    }

    @Override
    public int insertGameIndex(GameIndex gameIndex) {
        return gameIndexDao.insertGameIndex(gameIndex);
    }
}
