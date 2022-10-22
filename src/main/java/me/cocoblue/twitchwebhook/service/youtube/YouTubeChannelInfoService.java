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
    private final APIActionService apiActionService;

    public String updateUploadPlayListIdAndReturnUploadPlayListId(String channelId) {
        final String uploadPlayListId = apiActionService.getChannelUploadPlayListId(channelId);
        final YouTubeChannelInfoEntity youTubeChannelInfoEntity
                = youTubeChannelInfoRepository.getYouTubeChannelInfoEntityByYoutubeChannelId(channelId);

        youTubeChannelInfoEntity.setUploadPlaylistId(uploadPlayListId);
        youTubeChannelInfoRepository.save(youTubeChannelInfoEntity);

        return uploadPlayListId;
    }
}
