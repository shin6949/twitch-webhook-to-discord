package me.cocoblue.twitchwebhook.service.youtube;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.data.YouTubeSubscriptionType;
import me.cocoblue.twitchwebhook.domain.youtube.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Log4j2
@RequiredArgsConstructor
public class YouTubeUserLogService {
    private final YouTubeUserLogRepository youTubeUserLogRepository;
    private final YouTubeChannelInfoRepository youTubeChannelInfoRepository;
    private final YouTubeUserLogViewRepository youTubeUserLogViewRepository;

    @Async
    public void insertUserLog(YouTubeSubscriptionFormEntity notifyForm, YouTubeNotificationLogEntity youTubeNotificationLogEntity, boolean status) {
        final YouTubeUserLogEntity youTubeUserLogEntity = YouTubeUserLogEntity.builder()
                .logId(youTubeNotificationLogEntity)
                .youTubeSubscriptionFormEntity(notifyForm)
                .status(status)
                .build();

        youTubeUserLogRepository.save(youTubeUserLogEntity);
        if(youTubeUserLogEntity.getId() != 0) {
            log.info("YouTube User Log Insert Finished. Original Log ID is " + youTubeNotificationLogEntity.getId());
        } else {
            log.info("YouTube User Log Insert Failed. Original Log ID is " + youTubeNotificationLogEntity.getId());
        }
    }

    public boolean isNotInInterval(String youtubeChannelId, YouTubeSubscriptionType youTubeSubscriptionType, int intervalMinute) {
        // interval이 0일 경우 알림을 계속 받겠다는 의미
        if(intervalMinute == 0) {
            return true;
        }

        final LocalDateTime nowTime = LocalDateTime.now();
        final YouTubeChannelInfoEntity youTubeChannelInfoEntity = youTubeChannelInfoRepository.getYouTubeChannelInfoEntityByYoutubeChannelId(youtubeChannelId);

        final int notificationCount = youTubeUserLogViewRepository.countByYouTubeChannelInfoEntityAndYouTubeSubscriptionTypeAndReceivedTimeBetween
                (youTubeChannelInfoEntity, youTubeSubscriptionType, nowTime.minusMinutes(intervalMinute), nowTime);

        log.info("Notification Count: " + notificationCount);
        return notificationCount <= 0;
    }
}
