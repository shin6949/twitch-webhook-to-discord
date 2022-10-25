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
    private final YouTubeSubscriptionGroupViewRepository youTubeSubscriptionGroupViewRepository;
    private final YouTubeChannelInfoRepository youTubeChannelInfoRepository;
    private final YouTubeChannelInfoService youTubeChannelInfoService;
    private final APIActionService apiActionService;
    private final NewVideoNotifyService newVideoNotifyService;
    private final YouTubeSubscriptionFormRepository youTubeSubscriptionFormRepository;

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
        final List<YouTubeSubscriptionGroupViewEntity> youTubeSubscriptionGroupViewEntities = youTubeSubscriptionGroupViewRepository.findAllChannelId();
        log.info("formList Number: " + youTubeSubscriptionGroupViewEntities.size());

        for(YouTubeSubscriptionGroupViewEntity form: youTubeSubscriptionGroupViewEntities) {
            pubSubHubbubService.manageSubscription(form.getYouTubeChannelId(), true);
            log.info("Channel ID: " + form.getYouTubeChannelId() + " is registered.");
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
            channelLiveCheck(youTubeSubscriptionGroupViewEntity.getYouTubeChannelId());
        }
        log.info("YouTube Live Check Process Finished");
    }

    private void channelLiveCheck(String youtubeChannelId) {
        YouTubeChannelInfoEntity youTubeChannelInfoEntity = youTubeChannelInfoRepository.getYouTubeChannelInfoEntityByYoutubeChannelId(youtubeChannelId);

//        String youtubePlayListId = youTubeChannelInfoEntity.getUploadPlaylistId();
//        if(youtubePlayListId == null) {
//            log.info("Channel ID " + youTubeChannelInfoEntity.getYoutubeChannelId() + "'s upload playlist ID is NULL");
//            youtubePlayListId = youTubeChannelInfoService.updateUploadPlayListIdAndReturnUploadPlayListId(youTubeChannelInfoEntity);
//        }

        if(youTubeChannelInfoEntity.getUpcomingLiveId() != null) {
            final Video video = judgeLiveAndReturnVideo(youTubeChannelInfoEntity.getUpcomingLiveId());
            if(video != null) {
                log.info("Channel ID " + youTubeChannelInfoEntity.getYoutubeChannelId() + "'s New Live Found");
                final Channel channel = apiActionService.getChannelInfo(video.getSnippet().getChannelId());
                newVideoNotifyService.sendLiveStreamMessage(video, channel);
                youTubeChannelInfoService.clearUpcomingLiveId(youTubeChannelInfoEntity);
                return;
            }
        }

//        PlaylistItemListResponse playlistItemListResponse = apiActionService.getPlayListItem(youtubePlayListId, null);
//        Video video = null;
//        final LocalDateTime standardTime = youTubeChannelInfoEntity.getLastCheckedTime() != null
//                ? youTubeChannelInfoEntity.getLastCheckedTime() : LocalDateTime.now().minusMinutes(5).minusHours(9);
//        do {
//            final List<PlaylistItem> playlistItemList = new ArrayList<>(playlistItemListResponse.getItems());
//            video = getNewLiveItem(playlistItemList, standardTime);
//            if(video != null) break;
//
//            playlistItemListResponse = apiActionService.getPlayListItem(youtubePlayListId, playlistItemListResponse.getNextPageToken());
//        } while (playlistItemListResponse.getNextPageToken() != null);
//
//        youTubeChannelInfoEntity.setLastCheckedTime(LocalDateTime.now().minusHours(9));
//
//        if(video != null) {
//            log.info("Channel ID " + youTubeChannelInfoEntity.getYoutubeChannelId() + "'s New Live Found");
//            final Channel channel = apiActionService.getChannelInfo(video.getSnippet().getChannelId());
//            newVideoNotifyService.sendLiveStreamMessage(video, channel);
//
//            clearUpcomingLiveId(youTubeChannelInfoEntity);
//        } else {
//            youTubeChannelInfoRepository.save(youTubeChannelInfoEntity);
//        }
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

    private Video judgeLiveAndReturnVideo(String upcomingLiveId) {
        final Video video = apiActionService.getVideoInfo(upcomingLiveId);
        if(video.getSnippet().getLiveBroadcastContent().equals("live")) {
            return video;
        } else {
            return null;
        }
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
        final List<YouTubeSubscriptionGroupViewEntity> youTubeSubscriptionGroupViewEntities
                = youTubeSubscriptionGroupViewRepository.findAllByEnabledGroupByChannelId(false);

        for(YouTubeSubscriptionGroupViewEntity form: youTubeSubscriptionGroupViewEntities) {
            pubSubHubbubService.manageSubscription(form.getYouTubeChannelId(), true);
            form.setEnabled(true);
            youTubeSubscriptionFormRepository.updateEnabled(form.getYouTubeChannelId(), "true");
        }

        log.info("YouTube Event All Subscription Check Finished");
    }
}
