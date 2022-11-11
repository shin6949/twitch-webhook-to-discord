package me.cocoblue.twitchwebhook.service.youtube;

import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.Video;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.data.TwitchSubscriptionType;
import me.cocoblue.twitchwebhook.data.YouTubeSubscriptionType;
import me.cocoblue.twitchwebhook.domain.BroadcasterIdEntity;
import me.cocoblue.twitchwebhook.domain.youtube.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
public class YoutubeNotificationLogService {
    private final YouTubeNotificationLogRepository youTubeNotificationLogRepository;
    private final YouTubeChannelInfoRepository youTubeChannelInfoRepository;

    public boolean judgeDuplicateNotification(String videoId, String channelId) {
        /*
            true: Unique Notification
            false: Duplicated Notification
         */
        final YouTubeChannelInfoEntity youTubeChannelInfoEntity = youTubeChannelInfoRepository.getYouTubeChannelInfoEntityByYoutubeChannelId(channelId);
        final List<YouTubeNotificationLogEntity> youTubeNotificationLogEntities = youTubeNotificationLogRepository
                .findAllByYouTubeChannelInfoEntityAndVideoId(youTubeChannelInfoEntity, videoId);
        log.info("youTubeNotificationLogEntities Size: " + youTubeNotificationLogEntities.size());
        log.debug("youTubeNotificationLogEntities: " + youTubeNotificationLogEntities);
        return youTubeNotificationLogEntities.size() == 0;
    }

    @Transactional
    public YouTubeNotificationLogEntity insertLog(Video video, Channel channel, YouTubeSubscriptionType youTubeSubscriptionType) {
        YouTubeChannelInfoEntity youTubeChannelInfoEntity = youTubeChannelInfoRepository.getYouTubeChannelInfoEntityByYoutubeChannelId(channel.getId());

        YouTubeNotificationLogEntity youTubeNotificationLogEntity = YouTubeNotificationLogEntity
                .builder()
                .youTubeSubscriptionType(youTubeSubscriptionType)
                .youTubeChannelInfoEntity(youTubeChannelInfoEntity)
                .videoId(video.getId())
                .receivedTime(LocalDateTime.now())
                .build();

        log.debug("To Insert Log: " + youTubeNotificationLogEntity);
        final YouTubeNotificationLogEntity result = youTubeNotificationLogRepository.save(youTubeNotificationLogEntity);
        log.info("YouTube Notification Log Insert Processing Finished");

        return result;
    }
}
