package me.cocoblue.twitchwebhook.service.twitch;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.dto.twitch.eventsub.PostRequestBody;
import me.cocoblue.twitchwebhook.dto.twitch.webhook.Condition;
import me.cocoblue.twitchwebhook.dto.twitch.webhook.Transport;
import me.cocoblue.twitchwebhook.entity.StreamNotifyForm;
import me.cocoblue.twitchwebhook.vo.twitch.eventsub.SubscriptionList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Log4j2
@RequiredArgsConstructor
public class EventSubServiceImpl implements EventSubService {
    private final RequestService requestService;

    @Value("${twitch.client-id}")
    private String clientId;

    @Value("${twitch.client.scope.auth}")
    private String accessToken;

    @Value("${webapp.base.url}")
    private String webappBaseUrl;

    @Value("${twitch.event.secret}")
    private String webhookSecret;

    @Override
    public SubscriptionResponse getSubscriptionListFromTwitch() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Client-ID", clientId);
        headers.add("Authorization", "Bearer " + accessToken);

        final String subscriptionGetUrl = "https://api.twitch.tv/helix/eventsub/subscriptions";
        final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(subscriptionGetUrl);

        RestTemplate rt = new RestTemplate();
        ResponseEntity<SubscriptionResponse> response;

        try {
            response = rt.exchange(builder.toUriString(), HttpMethod.GET, new HttpEntity<>(headers), SubscriptionResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return response.getBody();
    }

    @Async
    @Override
    public void addEventSubToTwitch(StreamNotifyForm streamNotifyForm) {
        final String requestUrl = "https://api.twitch.tv/helix/eventsub/subscriptions";
        final String[] splitStr = streamNotifyForm.getType().split("\\.");
        StringBuilder callbackURL = new StringBuilder(webappBaseUrl + "/webhook");

        for(int i = 0; i < splitStr.length; i++) {
            if(i == 0) {
                callbackURL.append("/").append(splitStr[i]).append("/").append(streamNotifyForm.getBroadcasterId().getId());
            } else {
                callbackURL.append("/").append(splitStr[i]);
            }
        }

        final Condition condition = new Condition(Math.toIntExact(streamNotifyForm.getBroadcasterId().getId()));
        final Transport transport = new Transport(callbackURL.toString(), webhookSecret);
        final PostRequestBody postRequestBody = new PostRequestBody(streamNotifyForm.getType(), condition, transport);
        final HttpEntity<?> requestData = new HttpEntity<>(postRequestBody, requestService.makeRequestHeader(accessToken));

        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response;
        try {
            response = rt.exchange(requestUrl, HttpMethod.POST, requestData, String.class);
            log.info(response.getStatusCode());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
