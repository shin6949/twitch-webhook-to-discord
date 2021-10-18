package me.cocoblue.twitchwebhook.service;

import lombok.AllArgsConstructor;
import me.cocoblue.twitchwebhook.entity.GameIndexEntity;
import me.cocoblue.twitchwebhook.repository.GameIndexRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GameIndexServiceImpl implements GameIndexService {
    private final GameIndexRepository gameIndexRepository;

    @Override
    @Async
    public void insertGameIndex(GameIndexEntity gameIndexEntity) {
        gameIndexRepository.save(gameIndexEntity);
    }
}
