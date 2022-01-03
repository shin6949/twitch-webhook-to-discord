package me.cocoblue.twitchwebhook.service.twitch;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.dto.twitch.eventsub.SubscribeRequest;
import me.cocoblue.twitchwebhook.dto.twitch.eventsub.SubscriptionResponse;
import me.cocoblue.twitchwebhook.dto.twitch.webhook.Condition;
import me.cocoblue.twitchwebhook.dto.twitch.webhook.Transport;
import me.cocoblue.twitchwebhook.entity.SubscriptionFormEntity;
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

    @Value("${webapp.base-url}")
    private String webappBaseUrl;

    @Value("${twitch.event-secret}")
    private String webhookSecret;

    @Override
    public SubscriptionResponse getSubscriptionListFromTwitch(String accessToken) {
        log.info("Getting Subscription List From Twitch");

        final HttpHeaders headers = requestService.makeRequestHeader(accessToken);
        log.debug("Request Header: " + headers.toString());

        final String subscriptionGetUrl = "https://api.twitch.tv/helix/eventsub/subscriptions";
        final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(subscriptionGetUrl);
        log.debug("Request URL: " + builder.toUriString());

        RestTemplate rt = new RestTemplate();
        final ResponseEntity<SubscriptionResponse> response = rt.exchange(builder.toUriString(), HttpMethod.GET, new HttpEntity<>(headers), SubscriptionResponse.class);;

        log.debug("Response Body Data: " + response.getBody());
        return response.getBody();
    }

    @Async
    @Override
    public void addEventSubToTwitch(SubscriptionFormEntity subscriptionFormEntity, String accessToken) {
        log.info("Adding EventSub To Twitch");

        final HttpHeaders headers = requestService.makeRequestHeader(accessToken);
        log.debug("Request Header: " + headers.toString());

        final String requestUrl = "https://api.twitch.tv/helix/eventsub/subscriptions";
        log.debug("Request URL: " + requestUrl);

        final String[] splitStr = subscriptionFormEntity.getSubscriptionType().getTwitchName().split("\\.");
        StringBuilder callbackURL = new StringBuilder(webappBaseUrl + "/webhook");

        for(int i = 0; i < splitStr.length; i++) {
            if(i == 0) {
                callbackURL.append("/").append(splitStr[i]).append("/").append(subscriptionFormEntity.getBroadcasterIdEntity().getId());
            } else {
                callbackURL.append("/").append(splitStr[i]);
            }
        }

        final Condition condition = new Condition(String.valueOf(subscriptionFormEntity.getBroadcasterIdEntity().getId()));
        final Transport transport = new Transport(callbackURL.toString(), webhookSecret);
        final SubscribeRequest subscribeRequest = new SubscribeRequest(subscriptionFormEntity.getSubscriptionType().getTwitchName(), condition, transport);
        final HttpEntity<?> requestData = new HttpEntity<>(subscribeRequest, headers);
        log.debug("Request Body: " + requestData);

        RestTemplate rt = new RestTemplate();
        rt.exchange(requestUrl, HttpMethod.POST, requestData, String.class);
    }
}
