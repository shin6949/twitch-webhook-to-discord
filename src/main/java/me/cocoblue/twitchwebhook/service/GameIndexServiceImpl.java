package me.cocoblue.twitchwebhook.service;

import lombok.AllArgsConstructor;
import me.cocoblue.twitchwebhook.dao.GameIndexDao;
import me.cocoblue.twitchwebhook.dto.GameIndex;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GameIndexServiceImpl implements GameIndexService {
    private final GameIndexDao gameIndexDao;

    @Override
    public int insertGameIndex(GameIndex gameIndex) {
        return gameIndexDao.insertGameIndex(gameIndex);
    }
}
