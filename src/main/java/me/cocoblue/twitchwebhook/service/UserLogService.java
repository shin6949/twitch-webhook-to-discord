package me.cocoblue.twitchwebhook.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.domain.NotificationLogEntity;
import me.cocoblue.twitchwebhook.domain.SubscriptionFormEntity;
import me.cocoblue.twitchwebhook.domain.UserLogEntity;
import me.cocoblue.twitchwebhook.domain.UserLogRepository;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class UserLogService {
    private final UserLogRepository userLogRepository;

    @Async
    public void insertUserLog(SubscriptionFormEntity notifyForm, Long logId, HttpStatus httpStatus) {
        final NotificationLogEntity notificationLogEntity = NotificationLogEntity.builder().id(logId).build();

        final UserLogEntity userLogEntity = UserLogEntity.builder()
                .logId(notificationLogEntity)
                .logOwner(notifyForm.getFormOwner())
                .status(httpStatus.is2xxSuccessful())
                .result(httpStatus.toString())
                .build();

        userLogRepository.save(userLogEntity);
    }

}
