package me.cocoblue.twitchwebhook.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Log4j2
class FormServiceTest {
    @Autowired
    private FormService formService;

    @Test
    public void getStartFormByBroadcasterIdAndTypeTest() {
        log.info(formService.getFormByBroadcasterIdAndType(500843286, "stream.online"));
    }

    @Test
    public void getEndFormByBroadcasterIdAndTypeTest() {
        log.info(formService.getFormByBroadcasterIdAndType(500843286, "stream.offline"));
    }
}