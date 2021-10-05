package me.cocoblue.twitchwebhook.service.twitch;

import org.springframework.http.HttpEntity;

public interface RequestService {
    HttpEntity<?> makeRequestHeader(String accessToken);
}
