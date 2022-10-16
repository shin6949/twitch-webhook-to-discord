package me.cocoblue.twitchwebhook.service.youtube;

import com.google.api.services.youtube.model.Channel;
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
        final Channel channel = apiActionService.getChannelInfo(channelId);
        final YouTubeChannelInfoEntity youTubeChannelInfoEntity
                = youTubeChannelInfoRepository.getYouTubeChannelInfoEntityByYoutubeId(channelId);

        final String uploadPlayListId = channel.getContentDetails().getRelatedPlaylists().getUploads();
        youTubeChannelInfoEntity.setUploadPlaylistId(uploadPlayListId);
        youTubeChannelInfoRepository.save(youTubeChannelInfoEntity);

        return uploadPlayListId;
    }
}
