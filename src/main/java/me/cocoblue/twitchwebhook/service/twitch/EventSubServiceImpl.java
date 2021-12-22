package me.cocoblue.twitchwebhook.service.twitch;

import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.dto.twitch.eventsub.SubscribeRequest;
import me.cocoblue.twitchwebhook.dto.twitch.eventsub.SubscriptionResponse;
import me.cocoblue.twitchwebhook.dto.twitch.webhook.Condition;
import me.cocoblue.twitchwebhook.dto.twitch.webhook.Transport;
import me.cocoblue.twitchwebhook.entity.SubscriptionFormEntity;
import me.cocoblue.twitchwebhook.service.OauthTokenService;
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
public class EventSubServiceImpl implements EventSubService {
    private final RequestService requestService;
    private final OauthTokenService oauthTokenService;

    private String appToken;

    public EventSubServiceImpl(RequestService requestService, OauthTokenService oauthTokenService) {
        this.requestService = requestService;
        this.oauthTokenService = oauthTokenService;
        appToken = oauthTokenService.getAppTokenFromTwitch().getAccessToken();
    }

    @Value("${twitch.client-id}")
    private String clientId;

    @Value("${webapp.base.url}")
    private String webappBaseUrl;

    @Value("${twitch.event.secret}")
    private String webhookSecret;

    @Override
    public SubscriptionResponse getSubscriptionListFromTwitch() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Client-ID", clientId);
        headers.add("Authorization", "Bearer " + appToken);

        final String subscriptionGetUrl = "https://api.twitch.tv/helix/eventsub/subscriptions";
        final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(subscriptionGetUrl);

        RestTemplate rt = new RestTemplate();
        ResponseEntity<SubscriptionResponse> response;

        try {
            response = rt.exchange(builder.toUriString(), HttpMethod.GET, new HttpEntity<>(headers), SubscriptionResponse.class);
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            appToken = oauthTokenService.getAppTokenFromTwitch().getAccessToken();
            return getSubscriptionListFromTwitch();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        log.info(response.getBody());
        return response.getBody();
    }

    @Async
    @Override
    public void addEventSubToTwitch(SubscriptionFormEntity subscriptionFormEntity) {
        final String requestUrl = "https://api.twitch.tv/helix/eventsub/subscriptions";
        final String[] splitStr = subscriptionFormEntity.getSubscriptionTypeEntity().getTypeName().getTwitchName().split("\\.");
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
        final SubscribeRequest subscribeRequest = new SubscribeRequest(subscriptionFormEntity.getSubscriptionTypeEntity().getTypeName().getTwitchName(), condition, transport);
        final HttpEntity<?> requestData = new HttpEntity<>(subscribeRequest, requestService.makeRequestHeader(appToken));

        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response;
        try {
            response = rt.exchange(requestUrl, HttpMethod.POST, requestData, String.class);
            log.info(response.getStatusCode());
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            appToken = oauthTokenService.getAppTokenFromTwitch().getAccessToken();
            addEventSubToTwitch(subscriptionFormEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
