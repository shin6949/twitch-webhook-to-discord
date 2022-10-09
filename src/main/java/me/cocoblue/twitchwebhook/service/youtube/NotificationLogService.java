package me.cocoblue.twitchwebhook.service.youtube;

import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.Video;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.data.YouTubeSubscriptionType;
import me.cocoblue.twitchwebhook.domain.youtube.YouTubeNotificationLogEntity;
import me.cocoblue.twitchwebhook.domain.youtube.YouTubeNotificationRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Log4j2
@RequiredArgsConstructor
public class NotificationLogService {
    private final YouTubeNotificationRepository youTubeNotificationRepository;

    public boolean judgeDuplicateNotification(String videoId, String channelId) {
        /*
            true: Unique Notification
            false: Duplicated Notification
         */
        return youTubeNotificationRepository.findAllByChannelIdAndVideoId(videoId, channelId).size() == 0;
    }

    @Async
    public void insertLog(Video video, Channel channel, YouTubeSubscriptionType youTubeSubscriptionType) {
        YouTubeNotificationLogEntity youTubeNotificationLogEntity = YouTubeNotificationLogEntity
                .builder()
                .youTubeSubscriptionType(youTubeSubscriptionType)
                .channelId(channel.getId())
                .videoId(video.getId())
                .receivedTime(LocalDateTime.now())
                .build();

        youTubeNotificationRepository.save(youTubeNotificationLogEntity);
    }
}
