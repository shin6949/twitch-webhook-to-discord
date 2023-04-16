package me.cocoblue.twitchwebhook.service;

import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.domain.SubscriptionGroupViewRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Log4j2
class SubscriptionGroupViewTests {
    @Autowired
    SubscriptionGroupViewRepository subscriptionGroupViewRepository;

    @Test
    public void findAllTest() {
        log.info("result: " + subscriptionGroupViewRepository.findAll());
    }
}
