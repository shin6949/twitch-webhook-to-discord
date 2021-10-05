package me.cocoblue.twitchwebhook.service;

import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.service.twitch.UserInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Log4j2
class TwitchHelixApiServiceImplTest {
    @Autowired
    private UserInfoService userInfoService;

    @Test
    public void getUserInfoByBroadcasterIdFromTwitchTest() {
        log.info(userInfoService.getUserInfoByBroadcasterIdFromTwitch("500843286"));
    }

    @Test
    public void getUserInfoByLoginIdFromTwitchTest() {
        log.info(userInfoService.getUserInfoByLoginIdFromTwitch("soulofcoco"));
    }

}