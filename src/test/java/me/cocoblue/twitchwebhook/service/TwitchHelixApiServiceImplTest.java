package me.cocoblue.twitchwebhook.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TwitchHelixApiServiceImplTest {
    @Autowired
    private TwitchHelixApiServiceImpl twitchHelixApiService;

    @Test
    public void getUserInfoByBroadcasterIdFromTwitchTest() {
        System.out.println(twitchHelixApiService.getUserInfoByBroadcasterIdFromTwitch("500843286"));
    }

    @Test
    public void getUserInfoByLoginIdFromTwitchTest() {
        System.out.println(twitchHelixApiService.getUserInfoByLoginIdFromTwitch("soulofcoco"));
    }

}