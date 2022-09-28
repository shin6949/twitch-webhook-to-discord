package me.cocoblue.twitchwebhook.service.youtube;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service
@Log4j2
@RequiredArgsConstructor
public class PubSubHubbubService {
    @Value("${webapp.base-url}")
    private String webappBaseUrl;

    @Value("${twitch.event-secret}")
    private String eventSecret;

    public void manageSubscription(String channelId, boolean isAddSubscription) {
        final String pubSubHubbubUrl = "https://pubsubhubbub.appspot.com/subscribe";

        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("hub.callback", webappBaseUrl + "/webhook/youtube/" + channelId);
        parameters.add("hub.mode", isAddSubscription ? "subscribe" : "unsubscribe");
        parameters.add("hub.topic", "https://www.youtube.com/xml/feeds/videos.xml?channel_id=" + channelId);
        parameters.add("hub.secret", eventSecret);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity formEntity = new HttpEntity<>(parameters, headers);

        restTemplate.postForEntity(pubSubHubbubUrl, formEntity, String.class);
    }
}
