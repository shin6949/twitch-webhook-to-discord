package me.cocoblue.twitchwebhook.service.youtube;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.domain.youtube.YouTubeChannelInfoEntity;
import me.cocoblue.twitchwebhook.domain.youtube.YouTubeChannelInfoRepository;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class YouTubeChannelInfoService {
    private final YouTubeChannelInfoRepository youTubeChannelInfoRepository;

    public void updateUpcomingLiveIdByYoutubeChannelId(String upcomingLiveId, String youtubeChannelId) {
        YouTubeChannelInfoEntity youTubeChannelInfoEntity = youTubeChannelInfoRepository.getYouTubeChannelInfoEntityByYoutubeChannelId(youtubeChannelId);
        youTubeChannelInfoEntity.setUpcomingLiveId(upcomingLiveId);
        youTubeChannelInfoRepository.save(youTubeChannelInfoEntity);
    }

    public void clearUpcomingLiveId(YouTubeChannelInfoEntity youTubeChannelInfoEntity) {
        youTubeChannelInfoEntity.setUpcomingLiveId(null);
        youTubeChannelInfoRepository.save(youTubeChannelInfoEntity);
    }

    public void clearUpcomingLiveId(String channelId) {
        YouTubeChannelInfoEntity youTubeChannelInfoEntity = youTubeChannelInfoRepository.getYouTubeChannelInfoEntityByYoutubeChannelId(channelId);
        youTubeChannelInfoEntity.setUpcomingLiveId(null);
        youTubeChannelInfoRepository.save(youTubeChannelInfoEntity);
    }
}
