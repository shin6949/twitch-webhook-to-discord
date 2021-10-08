package me.cocoblue.twitchwebhook.service.twitch;

import org.springframework.http.HttpHeaders;

public interface RequestService {
    HttpHeaders makeRequestHeader(String accessToken);
}
