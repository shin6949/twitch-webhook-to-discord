package me.cocoblue.twitchwebhook.service.twitch;

import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.vo.twitch.eventsub.SubscriptionList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Log4j2
public class SubscriptionListServiceImpl implements SubscriptionListService {
    @Value("${twitch.client-id}")
    private String clientId;

    @Value("${twitch.client.scope.auth}")
    private String accessToken;

    @Override
    public SubscriptionList getSubscriptionListFromTwitch() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Client-ID", clientId);
        headers.add("Authorization", "Bearer " + accessToken);

        final String subscriptionGetUrl = "https://api.twitch.tv/helix/eventsub/subscriptions";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(subscriptionGetUrl);

        RestTemplate rt = new RestTemplate();
        ResponseEntity<SubscriptionList> response;

        try {
            response = rt.exchange(builder.toUriString(), HttpMethod.GET, new HttpEntity<>(headers), SubscriptionList.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return response.getBody();
    }
}
