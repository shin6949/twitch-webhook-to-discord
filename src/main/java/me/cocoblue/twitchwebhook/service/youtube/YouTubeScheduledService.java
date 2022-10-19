package me.cocoblue.twitchwebhook.service.youtube;

import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.data.YouTubeSubscriptionType;
import me.cocoblue.twitchwebhook.domain.youtube.YouTubeSubscriptionFormEntity;
import me.cocoblue.twitchwebhook.domain.youtube.YouTubeSubscriptionFormRepository;
import me.cocoblue.twitchwebhook.domain.youtube.YouTubeSubscriptionGroupViewEntity;
import me.cocoblue.twitchwebhook.domain.youtube.YouTubeSubscriptionGroupViewRepository;
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
    private final YouTubeChannelInfoService youTubeChannelInfoService;
    private final APIActionService apiActionService;

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
                = youTubeSubscriptionGroupViewRepository.findAllYouTubeSubscriptionType(YouTubeSubscriptionType.LIVE_START);

        for(YouTubeSubscriptionGroupViewEntity youTubeSubscriptionGroupViewEntity : youTubeSubscriptionGroupViewEntities) {
            channelLiveCheck(youTubeSubscriptionGroupViewEntity);
        }
    }

    private void channelLiveCheck(YouTubeSubscriptionGroupViewEntity youTubeSubscriptionGroupViewEntity) {
        String youtubePlayListId = youTubeSubscriptionGroupViewEntity.getUploadPlayListId();
        if(youtubePlayListId == null) {
            youtubePlayListId = youTubeChannelInfoService.updateUploadPlayListIdAndReturnUploadPlayListId(youTubeSubscriptionGroupViewEntity.getYouTubeChannelId());
        }

        PlaylistItemListResponse playlistItemListResponse = apiActionService.getPlayListItem(youtubePlayListId, null);
        do {
            final List<PlaylistItem> playlistItemList = new ArrayList<PlaylistItem>(playlistItemListResponse.getItems());

            playlistItemListResponse = apiActionService.getPlayListItem(youtubePlayListId, playlistItemListResponse.getNextPageToken());
        } while (playlistItemListResponse.getNextPageToken() != null);
        List<PlaylistItem> playlistItemList = new ArrayList<PlaylistItem>();
    }

    private boolean isNewVideo(PlaylistItem playlistItem, LocalDateTime standardTime) {
        LocalDateTime videoPublishTime = LocalDateTime.ofInstant(Instant.parse(playlistItem.getSnippet().getPublishedAt().toStringRfc3339()), ZoneOffset.UTC);
        return videoPublishTime.isAfter(standardTime);
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
