package me.cocoblue.twitchwebhook.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Log4j2
class TwitchHelixApiServiceImplTest {
    @Autowired
    private TwitchHelixApiService twitchHelixApiService;

    @Test
    public void getUserInfoByBroadcasterIdFromTwitchTest() {
        log.info(twitchHelixApiService.getUserInfoByBroadcasterIdFromTwitch("500843286"));
    }

    @Test
    public void getUserInfoByLoginIdFromTwitchTest() {
        log.info(twitchHelixApiService.getUserInfoByLoginIdFromTwitch("soulofcoco"));
    }

}