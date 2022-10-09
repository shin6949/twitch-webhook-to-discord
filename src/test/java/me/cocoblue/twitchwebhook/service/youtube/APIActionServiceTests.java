package me.cocoblue.twitchwebhook.service.youtube;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Log4j2
class APIActionServiceTests {
    @Autowired
    private APIActionService APIActionService;

    @Value("${youtube.api-key}")
    private String youtubeAPIKey;

//    @Test
//    public void getVideoInfoTest() {
//        log.info(youTubeAPIActionService.getVideoInfo("alLs9S4pwo0"));
//    }

    @Test
    public void getAPIKeyTest() {
        log.info(APIActionService.getAPIKey());
    }
}
