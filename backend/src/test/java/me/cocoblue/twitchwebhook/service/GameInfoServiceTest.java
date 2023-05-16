package me.cocoblue.twitchwebhook.service;

import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.dto.twitch.Game;
import me.cocoblue.twitchwebhook.service.twitch.GameInfoService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Log4j2
public class GameInfoServiceTest {
    /*
        This test need to use twitch main API.
     */
    @Autowired
    private GameInfoService gameInfoService;

    @Test
    public void getJustChattingTest() {
        final Game game = gameInfoService.getGameInfoByIdFromTwitch("509658");
        Assertions.assertEquals(game.getName(), "Just Chatting");
        Assertions.assertEquals(game.getBoxArtUrl(), "https://static-cdn.jtvnw.net/ttv-boxart/509658-{width}x{height}.jpg");
        Assertions.assertEquals(game.removeSizeMentionInBoxArtUrl(), "https://static-cdn.jtvnw.net/ttv-boxart/509658.jpg");
    }

    @Test
    public void getStrayTest() {
        final Game game = gameInfoService.getGameInfoByIdFromTwitch("518006");
        Assertions.assertEquals(game.getName(), "Stray");
        Assertions.assertEquals(game.getBoxArtUrl(), "https://static-cdn.jtvnw.net/ttv-boxart/518006_IGDB-{width}x{height}.jpg");
        Assertions.assertEquals(game.removeSizeMentionInBoxArtUrl(), "https://static-cdn.jtvnw.net/ttv-boxart/518006_IGDB.jpg");
    }
}
