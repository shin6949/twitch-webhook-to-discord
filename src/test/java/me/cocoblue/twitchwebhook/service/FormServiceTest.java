package me.cocoblue.twitchwebhook.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FormServiceTest {
    @Autowired
    private FormService formService;

    @Test
    public void getStartFormByBroadcasterIdAndTypeTest() {
        System.out.println(formService.getStartFormByBroadcasterIdAndType(500843286, 0));
    }

    @Test
    public void getEndFormByBroadcasterIdAndTypeTest() {
        System.out.println(formService.getEndFormByBroadcasterIdAndType(500843286, 0));
    }
}