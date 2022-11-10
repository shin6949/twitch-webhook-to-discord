package me.cocoblue.twitchwebhook.service;

import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.domain.*;
import me.cocoblue.twitchwebhook.service.twitch.TwitchUserLogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@Log4j2
class TwitchUserLogServiceTests {
    @Autowired
    private TwitchUserLogService twitchUserLogService;

    @Autowired
    private BroadcasterIdRepository broadcasterIdRepository;

    @Autowired
    private SubscriptionFormRepository subscriptionFormRepository;

    @Autowired
    private NotificationLogRepository notificationLogRepository;

    @Test
    public void insertUserLogTest() {
        Optional<BroadcasterIdEntity> broadcasterIdEntity = broadcasterIdRepository.getBroadcasterIdEntityByIdEquals(672681145);
        if(broadcasterIdEntity.isEmpty()) {
            log.error("Can't Find broadcasterIdEntity!");
        }

        PageRequest pageRequest = PageRequest.of(1, 1);
        Page<NotificationLogEntity> notificationLogEntities = notificationLogRepository.getNotificationLogEntitiesByBroadcasterIdEntityOrderByReceivedTime(broadcasterIdEntity.get(), pageRequest);
        List<SubscriptionFormEntity> subscriptionFormEntities = subscriptionFormRepository.getSubscriptionFormEntityByBroadcasterIdEntity(broadcasterIdEntity.get());

        for(SubscriptionFormEntity subscriptionFormEntity : subscriptionFormEntities) {
            twitchUserLogService.insertUserLog(subscriptionFormEntity, notificationLogEntities.getContent().get(0), true);
        }
    }
}
