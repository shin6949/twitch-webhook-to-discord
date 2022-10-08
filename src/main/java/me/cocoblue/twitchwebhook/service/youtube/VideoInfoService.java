package me.cocoblue.twitchwebhook.service.youtube;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Value;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.VideoListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
@Log4j2
@RequiredArgsConstructor
public class VideoInfoService {
    @Value("${youtube.api-key}")
    private final String youtubeAPIKey;

    public boolean judgeLiveContentByVideoId(String videoId) {
        final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

        try {
            final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            YouTube youtubeService = new YouTube.Builder(httpTransport, JSON_FACTORY, null)
                    .setApplicationName("Webhook API")
                    .build();

            YouTube.Videos.List request = youtubeService.videos()
                    .list(Collections.singletonList("snippet,contentDetails,statistics"));
            VideoListResponse response = request
                    .setId(Collections.singletonList(videoId))
                    .setKey(youtubeAPIKey)
                    .execute();

            return response.getItems().get(0).getSnippet().getLiveBroadcastContent().equals("live");

        } catch (GeneralSecurityException | IOException generalSecurityException)  {
            generalSecurityException.printStackTrace();
            return false;
        }
    }
}
