package me.cocoblue.twitchwebhook.service.twitch;

import lombok.RequiredArgsConstructor;
import me.cocoblue.twitchwebhook.entity.OauthTokenEntity;
import me.cocoblue.twitchwebhook.service.OauthTokenService;
import me.cocoblue.twitchwebhook.dto.twitch.Channel;
import me.cocoblue.twitchwebhook.dto.twitch.ChannelResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class ChannelInfoServiceImpl implements ChannelInfoService {
    private final OauthTokenService oauthTokenService;
    private final RequestService requestService;

    @Override
    public Channel getChannelInformationByBroadcasterId(String broadcasterId) {
        OauthTokenEntity oauthTokenEntity = oauthTokenService.getRecentOauthToken();
        Channel channel = requestChannelInformationFromTwitch(broadcasterId, oauthTokenEntity.getAccessToken());

        if (channel == null) {
            oauthTokenEntity = oauthTokenService.getOauthTokenFromTwitch();
            channel = requestChannelInformationFromTwitch(broadcasterId, oauthTokenEntity.getAccessToken());
        }

        return channel;
    }

    private Channel requestChannelInformationFromTwitch(String broadcasterId, String accessToken) {
        final String channelGetUrl = "https://api.twitch.tv/helix/channels";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(channelGetUrl)
                .queryParam("broadcaster_id", broadcasterId);

        HttpEntity<?> entity = new HttpEntity<>(requestService.makeRequestHeader(accessToken));

        RestTemplate rt = new RestTemplate();
        ResponseEntity<ChannelResponse> response;

        try {
            response = rt.exchange(builder.toUriString(), HttpMethod.GET, entity, ChannelResponse.class);
        } catch (HttpClientErrorException.Unauthorized e) {
            return null;
        }

        return response.getBody().getFirstData();
    }
}
