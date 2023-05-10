package me.cocoblue.twitchwebhook.service.twitch;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.dto.twitch.AppTokenResponse;
import me.cocoblue.twitchwebhook.dto.twitch.Channel;
import me.cocoblue.twitchwebhook.dto.twitch.ChannelResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Objects;

@Log4j2
@Service
@RequiredArgsConstructor
public class ChannelInfoService {
    private final OauthTokenService oauthTokenService;
    private final RequestService requestService;

    @Value("${twitch.api-endpoint}")
    private String twitchApiUrl;

    public Channel getChannelInformationByBroadcasterId(String broadcasterId) {
        log.info("Getting Channel Information Twitch By Broadcaster ID");

        final AppTokenResponse appTokenResponse = oauthTokenService.getAppTokenFromTwitch();
        final String accessToken = appTokenResponse.getAccessToken();

        final String channelGetUrl = twitchApiUrl + "/channels";
        final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(channelGetUrl)
                .queryParam("broadcaster_id", broadcasterId);
        log.debug("Built URL: " + builder.toUriString());

        final HttpEntity<?> entity = new HttpEntity<>(requestService.makeRequestHeader(accessToken));

        final RestTemplate rt = new RestTemplate();
        final ResponseEntity<ChannelResponse> response = rt.exchange(builder.toUriString(), HttpMethod.GET, entity, ChannelResponse.class);

        oauthTokenService.revokeAppTokenToTwitch(accessToken);

        final Channel channel = Objects.requireNonNull(response.getBody()).getFirstData();
        log.info("Channel Information Received");
        log.debug("Received Channel Information: " + channel);

        return channel;
    }
}
