package me.cocoblue.twitchwebhook.service.twitch;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

@Service
public class RequestService {
    @Value("${twitch.client-id}")
    private String clientId;

    public HttpHeaders makeRequestHeader(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Client-ID", clientId);
        headers.setBearerAuth(accessToken);

        return headers;
    }
}
