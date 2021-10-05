package me.cocoblue.twitchwebhook.service;

import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.dto.twitch.WebhookRequestForm;
import me.cocoblue.twitchwebhook.dto.twitch.webhook.Condition;
import me.cocoblue.twitchwebhook.dto.twitch.webhook.Transport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Log4j2
@Service
@Component
public class WebhookRenewService {
    private final OauthTokenService oauthTokenService;
    private final FormService formService;

    public WebhookRenewService(OauthTokenService oauthTokenService, FormService formService) {
        this.oauthTokenService = oauthTokenService;
        this.formService = formService;
    }

    @Value("${twitch.client-id}")
    private String clientId;

    @Value("${webapp.base.url}")
    private String webappBaseUrl;

    @Value("${twitch.hub.secret}")
    private String webhookSecret;

//    @Scheduled(cron = "0 0 */1 * * *")
    public void RenewCronjob() {
        // 지금은 방송 알림만 지원
        log.info("Doing Scheduled Job");

        // DB에서 Broadcaster ID를 모두 받아옴. (중복x)
        List<Integer> broadcasterIdList = formService.getAllBroadcasterId();
        log.info(broadcasterIdList);

        String accessToken = oauthTokenService.getOauthTokenFromTwitch().getAccessToken();
        for (Integer broadcasterId : broadcasterIdList) {
            HttpHeaders headers = configureRequestHeader(accessToken);

            postToTwitch(new HttpEntity<>(configureRequestBody(broadcasterId.toString(), "unsubscribe"), headers));
            postToTwitch(new HttpEntity<>(configureRequestBody(broadcasterId.toString(), "subscribe"), headers));
        }
    }

    private void postToTwitch(HttpEntity<WebhookRequestForm> entity) {
        RestTemplate rt = new RestTemplate();
        String webhookUrl = "https://api.twitch.tv/helix/webhooks/hub";
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(webhookUrl);

        log.info(entity);
        try {
            rt.exchange(uriComponentsBuilder.toUriString(), HttpMethod.POST, entity, String.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HttpHeaders configureRequestHeader(String accessToken) {
        HttpHeaders result = new HttpHeaders();
        result.add("Content-Type", "application/json");
        result.add("Client-id", clientId);
        result.add("Authorization", "Bearer " + accessToken);

        return result;
    }

    private WebhookRequestForm configureRequestBody(String broadcasterId, String mode) {
        final String type = "stream.online";
        final String callbackUrl = webappBaseUrl + "/webhook/stream/" + broadcasterId;

        WebhookRequestForm webhookRequestForm = new WebhookRequestForm(type, new Condition(broadcasterId), new Transport(callbackUrl, webhookSecret));
        log.info(webhookRequestForm);
        return webhookRequestForm;
    }
}
