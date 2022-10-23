package me.cocoblue.twitchwebhook.service.youtube;

import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.Video;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.data.YouTubeSubscriptionType;
import me.cocoblue.twitchwebhook.domain.youtube.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
@Component
@RequiredArgsConstructor
public class YouTubeScheduledService {
    @Value("${twitch.event-renew}")
    private boolean eventEnabled;
    private final PubSubHubbubService pubSubHubbubService;
    private final YouTubeSubscriptionFormRepository youTubeSubscriptionFormRepository;
    private final YouTubeSubscriptionGroupViewRepository youTubeSubscriptionGroupViewRepository;
    private final YouTubeChannelInfoRepository youTubeChannelInfoRepository;
    private final YouTubeChannelInfoService youTubeChannelInfoService;
    private final APIActionService apiActionService;
    private final NewVideoNotifyService newVideoNotifyService;

    @Scheduled(cron = "0 20 6 */1 * *")
    public void youtubeAllSubscriptionCheck() {
        /*
            하루 주기로 모든 구독들을 갱신함.
         */
        if(!eventEnabled) {
            log.info("Event Renew Function Disabled. Do Not Processing.");
            return;
        }

        log.info("YouTube Event All Subscription Check Start");
        final List<YouTubeSubscriptionFormEntity> youTubeSubscriptionFormEntityList
                = youTubeSubscriptionFormRepository.findAll();
        log.info("formList Number: " + youTubeSubscriptionFormEntityList.size());

        for(YouTubeSubscriptionFormEntity form: youTubeSubscriptionFormEntityList) {
            pubSubHubbubService.manageSubscription(form.getYouTubeChannelInfoEntity().getYoutubeChannelId(), true);
            log.info("Channel ID: " + form.getYouTubeChannelInfoEntity().getYoutubeChannelId() + " is registered.");
        }

        log.info("YouTube Event All Subscription Check Finished");
    }

    @Scheduled(cron = "0 */5 * * * *")
    public void liveCheck() {
        log.info("YouTube Live Check Process Start");

        log.info("Getting Subscription Form");
        final List<YouTubeSubscriptionGroupViewEntity> youTubeSubscriptionGroupViewEntities
                = youTubeSubscriptionGroupViewRepository.findAllByYouTubeSubscriptionType(YouTubeSubscriptionType.LIVE_START);

        for(YouTubeSubscriptionGroupViewEntity youTubeSubscriptionGroupViewEntity : youTubeSubscriptionGroupViewEntities) {
            channelLiveCheck(youTubeSubscriptionGroupViewEntity);
        }
        log.info("YouTube Live Check Process Finished");
    }

    private void channelLiveCheck(YouTubeSubscriptionGroupViewEntity youTubeSubscriptionGroupViewEntity) {
        String youtubePlayListId = youTubeSubscriptionGroupViewEntity.getUploadPlayListId();
        if(youtubePlayListId == null) {
            log.info("Channel ID " + youTubeSubscriptionGroupViewEntity.getYouTubeChannelId() + "'s upload playlist ID is NULL");
            youtubePlayListId = youTubeChannelInfoService.updateUploadPlayListIdAndReturnUploadPlayListId(youTubeSubscriptionGroupViewEntity.getYouTubeChannelId());
        }

        PlaylistItemListResponse playlistItemListResponse = apiActionService.getPlayListItem(youtubePlayListId, null);
        Video video = null;
        final LocalDateTime standardTime = youTubeSubscriptionGroupViewEntity.getLastCheckedTime() != null
                ? youTubeSubscriptionGroupViewEntity.getLastCheckedTime() : LocalDateTime.now().minusMinutes(5).minusHours(9);
        do {
            final List<PlaylistItem> playlistItemList = new ArrayList<>(playlistItemListResponse.getItems());
            video = getNewLiveItem(playlistItemList, standardTime);
            if(video != null) break;

            playlistItemListResponse = apiActionService.getPlayListItem(youtubePlayListId, playlistItemListResponse.getNextPageToken());
        } while (playlistItemListResponse.getNextPageToken() != null);

        YouTubeChannelInfoEntity youTubeChannelInfoEntity = youTubeChannelInfoRepository.getYouTubeChannelInfoEntityByYoutubeChannelId(youTubeSubscriptionGroupViewEntity.getYouTubeChannelId());
        youTubeChannelInfoEntity.setLastCheckedTime(LocalDateTime.now().minusHours(9));
        youTubeChannelInfoRepository.save(youTubeChannelInfoEntity);

        if(video == null) {
            return;
        }

        log.info("Channel ID " + youTubeSubscriptionGroupViewEntity.getYouTubeChannelId() + "'s New Live Found");
        final Channel channel = apiActionService.getChannelInfo(video.getSnippet().getChannelId());
        newVideoNotifyService.sendLiveStreamMessage(video, channel);
    }

    private Video getNewLiveItem(List<PlaylistItem> playlistItemList, LocalDateTime standardTime) {
        // standardTime: 이 시간 이후로 올라온 것만 스캔
        for(PlaylistItem playlistItem : playlistItemList) {
            log.debug("playlistItem: " + playlistItem);
            final LocalDateTime videoPublishTime = LocalDateTime.ofInstant(Instant.parse(playlistItem.getSnippet().getPublishedAt().toStringRfc3339()), ZoneOffset.UTC);
            // 비디오가 기준 시간보다 과거에 올라온 경우 다음 것을 처리
            if(videoPublishTime.isBefore(standardTime)) {
                break;
            }

            final Video videoInfo = apiActionService.getVideoInfo(playlistItem.getContentDetails().getVideoId());
            log.debug("Video: " + videoInfo);
            if(videoInfo.getSnippet().getLiveBroadcastContent().equals("live")) return videoInfo;
        }
        return null;
    }

    @Scheduled(cron = "0 */5 * * * *")
    public void youtubeNotEnabledSubscriptionCheck() {
        /*
            5분 주기로 비활성 구독들을 확인하고 활성화 시킴.
         */
        if(!eventEnabled) {
            log.info("Event Renew Function Disabled. Do Not Processing.");
            return;
        }

        log.info("YouTube Event Not Enabled Subscription Check Start");
        final List<YouTubeSubscriptionFormEntity> youTubeSubscriptionFormEntityList
                = youTubeSubscriptionFormRepository.findAllByEnabled(false);

        for(YouTubeSubscriptionFormEntity form: youTubeSubscriptionFormEntityList) {
            pubSubHubbubService.manageSubscription(form.getYouTubeChannelInfoEntity().getYoutubeChannelId(), true);
            form.setEnabled(true);
            youTubeSubscriptionFormRepository.save(form);
        }

        log.info("YouTube Event All Subscription Check Finished");
    }
}
