package me.cocoblue.twitchwebhook.service;

import me.cocoblue.twitchwebhook.dto.twitch.eventsub.stream.Body;
import org.springframework.http.HttpHeaders;

public interface ControllerProcessingService {
    boolean dataNotValid(HttpHeaders headers, String notification);
    boolean isChallenge(Body body);
}
