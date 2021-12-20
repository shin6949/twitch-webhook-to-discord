package me.cocoblue.twitchwebhook.service.twitch;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

@Service
public class RequestServiceImpl implements RequestService{
    @Value("${twitch.client-id}")
    private String clientId;

    @Override
    public HttpHeaders makeRequestHeader(String accessToken) {
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Client-ID", clientId);
        headers.setBearerAuth(accessToken);

        return headers;
    }
}
