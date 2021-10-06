package me.cocoblue.twitchwebhook.service.twitch;

import lombok.RequiredArgsConstructor;
import me.cocoblue.twitchwebhook.dto.OauthToken;
import me.cocoblue.twitchwebhook.service.OauthTokenService;
import me.cocoblue.twitchwebhook.vo.twitch.Channel;
import me.cocoblue.twitchwebhook.vo.twitch.ChannelResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ChannelInfoServiceImpl implements ChannelInfoService {
    private final OauthTokenService oauthTokenService;
    private final RequestService requestService;

    @Override
    public Channel getChannelInformationByBroadcasterId(String broadcasterId) {
        OauthToken oauthToken = oauthTokenService.getRecentOauthToken();
        Channel channel = requestChannelInformationFromTwitch(broadcasterId, oauthToken.getAccessToken());

        if (channel == null) {
            oauthToken = oauthTokenService.getOauthTokenFromTwitch();
            channel = requestChannelInformationFromTwitch(broadcasterId, oauthToken.getAccessToken());
        }

        return channel;
    }

    private Channel requestChannelInformationFromTwitch(String broadcasterId, String accessToken) {
        final String channelGetUrl = "https://api.twitch.tv/helix/channels";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(channelGetUrl)
                .queryParam("broadcaster_id", broadcasterId);

        HttpEntity<?> entity = requestService.makeRequestHeader(accessToken);

        RestTemplate rt = new RestTemplate();
        ResponseEntity<ChannelResponse> response;

        try {
            response = rt.exchange(builder.toUriString(), HttpMethod.GET, entity, ChannelResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return response.getBody().getFirstData();
    }
}
