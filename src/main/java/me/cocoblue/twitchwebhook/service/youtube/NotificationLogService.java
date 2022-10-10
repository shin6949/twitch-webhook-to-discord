package me.cocoblue.twitchwebhook.service.youtube;

import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.Video;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.data.YouTubeSubscriptionType;
import me.cocoblue.twitchwebhook.domain.youtube.YouTubeNotificationLogEntity;
import me.cocoblue.twitchwebhook.domain.youtube.YouTubeNotificationLogRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class NotificationLogService {
    private final YouTubeNotificationLogRepository youTubeNotificationLogRepository;

    public boolean judgeDuplicateNotification(String videoId, String channelId) {
        /*
            true: Unique Notification
            false: Duplicated Notification
         */
        final List<YouTubeNotificationLogEntity> youTubeNotificationLogEntities = youTubeNotificationLogRepository.findAllByChannelIdAndVideoId(channelId, videoId);
        log.info("youTubeNotificationLogEntities Size: " + youTubeNotificationLogEntities.size());
        log.debug("youTubeNotificationLogEntities: " + youTubeNotificationLogEntities);
        return youTubeNotificationLogEntities.size() == 0;
    }

    @Async
    @Transactional
    public void insertLog(Video video, Channel channel, YouTubeSubscriptionType youTubeSubscriptionType) {
        YouTubeNotificationLogEntity youTubeNotificationLogEntity = YouTubeNotificationLogEntity
                .builder()
                .youTubeSubscriptionType(youTubeSubscriptionType)
                .channelId(channel.getId())
                .videoId(video.getId())
                .receivedTime(LocalDateTime.now())
                .build();

        youTubeNotificationLogRepository.save(youTubeNotificationLogEntity);
    }
}
