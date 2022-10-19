package me.cocoblue.twitchwebhook.service.youtube;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.model.*;
import org.springframework.beans.factory.annotation.Value;
import com.google.api.services.youtube.YouTube;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class APIActionService {
    @Value("${youtube.api-key}")
    private String apiKey;

    public Video getVideoInfo(String videoId) {
        try {
            final YouTube youTubeService = getYouTubeInstance();
            assert youTubeService != null;

            YouTube.Videos.List request = youTubeService.videos()
                    .list(Collections.singletonList("snippet,contentDetails,statistics"));

            log.debug("YouTube API Key: " + apiKey);
            VideoListResponse response = request
                    .setId(Collections.singletonList(videoId))
                    .setKey(apiKey)
                    .execute();

            return response.getItems().get(0);

        } catch (Exception ioException)  {
            ioException.printStackTrace();
            return null;
        }
    }

    public Channel getChannelInfo(String channelId) {
        try {
            YouTube youtubeService = getYouTubeInstance();
            assert youtubeService != null;

            YouTube.Channels.List request = youtubeService.channels()
                    .list(Collections.singletonList("id, snippet, contentDetails"));

            log.debug("YouTube API Key: " + apiKey);
            ChannelListResponse response = request
                    .setId(Collections.singletonList(channelId))
                    .setKey(apiKey)
                    .execute();

            return response.getItems().get(0);
        } catch (IOException generalSecurityException)  {
            generalSecurityException.printStackTrace();
            return null;
        }
    }

    public PlaylistItemListResponse getPlayListItem(String playlistId, String nextToken) {
        try {
            YouTube youtubeService = getYouTubeInstance();
            assert youtubeService != null;

            YouTube.PlaylistItems.List playlistItemRequest =
                    youtubeService.playlistItems()
                            .list(Collections.singletonList("id,contentDetails,snippet"))
                            .setKey(apiKey)
                            .setFields("items(snippet,contentDetails),nextPageToken,pageInfo")
                            .setMaxResults(50L)
                            .setPlaylistId(playlistId)
                            .setPageToken(nextToken);

            return playlistItemRequest.execute();
        } catch (IOException generalSecurityException)  {
            generalSecurityException.printStackTrace();
            return null;
        }
    }

    public String getChannelUploadPlayListId(String channelId) {
        final Channel channel = getChannelInfo(channelId);
        return channel.getContentDetails().getRelatedPlaylists().getUploads();
    }

    private YouTube getYouTubeInstance() {
        try {
            final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
            final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

            return new YouTube.Builder(httpTransport, JSON_FACTORY, null)
                    .setApplicationName("Webhook API")
                    .build();
        } catch (GeneralSecurityException | IOException generalSecurityException)  {
            generalSecurityException.printStackTrace();
            return null;
        }
    }
}
