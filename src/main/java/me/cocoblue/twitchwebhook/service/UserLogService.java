package me.cocoblue.twitchwebhook.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.domain.NotificationLogEntity;
import me.cocoblue.twitchwebhook.domain.SubscriptionFormEntity;
import me.cocoblue.twitchwebhook.domain.UserLogEntity;
import me.cocoblue.twitchwebhook.domain.UserLogRepository;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class UserLogService {
    private final UserLogRepository userLogRepository;

    public void insertUserLog(SubscriptionFormEntity notifyForm, NotificationLogEntity notificationLogEntity, boolean status) {
        final UserLogEntity userLogEntity = UserLogEntity.builder()
                .logId(notificationLogEntity)
                .subscriptionFormEntity(notifyForm)
                .status(status)
                .build();

        userLogRepository.save(userLogEntity);
    }

}
