package me.cocoblue.twitchwebhook.service.twitch;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;

@Service
public class RequestServiceImpl implements RequestService{
    @Value("${twitch.client-id}")
    private String clientId;

    public HttpEntity<?> makeRequestHeader(String accessToken) {
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Client-ID", clientId);
        headers.add("Authorization", "Bearer " + accessToken);

        return new HttpEntity<>(headers);
    }
}
