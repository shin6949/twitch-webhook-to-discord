package me.cocoblue.twitchwebhook.service.youtube;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.domain.youtube.YouTubeNotificationLogEntity;
import me.cocoblue.twitchwebhook.domain.youtube.YouTubeSubscriptionFormEntity;
import me.cocoblue.twitchwebhook.domain.youtube.YouTubeUserLogEntity;
import me.cocoblue.twitchwebhook.domain.youtube.YouTubeUserLogRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class YouTubeUserLogService {
    private final YouTubeUserLogRepository youTubeUserLogRepository;

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

}
