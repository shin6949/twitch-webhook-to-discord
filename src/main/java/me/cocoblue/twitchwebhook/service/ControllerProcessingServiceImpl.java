package me.cocoblue.twitchwebhook.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.dto.twitch.eventsub.StreamNotifyRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Log4j2
@AllArgsConstructor
public class ControllerProcessingServiceImpl implements ControllerProcessingService {
    private final EncryptDataService encryptDataService;

    @Override
    public boolean dataNotValid(HttpHeaders headers, String notification) {
        final String signature = Objects.requireNonNull(headers.get("twitch-eventsub-message-signature")).get(0);

        final String data = headers.get("Twitch-Eventsub-Message-Id").get(0) +
                headers.get("Twitch-Eventsub-Message-Timestamp").get(0) + notification;

        final String encryptValue = "sha256=" + encryptDataService.encryptString(data);
        log.info("Received Signature: " + signature);
        log.info("Encrypt Value: " + encryptValue);

        return !encryptValue.equals(signature);
    }

    @Override
    public boolean isChallenge(StreamNotifyRequest.Body body) {
        return body.getChallenge() != null &&
                body.getSubscription().getStatus().equals("webhook_callback_verification_pending");
    }
}
