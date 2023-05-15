package me.cocoblue.twitchwebhook.service.twitch;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.dto.twitch.eventsub.StreamNotifyRequestBody;
import me.cocoblue.twitchwebhook.service.EncryptDataService;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Log4j2
@AllArgsConstructor
public class ControllerProcessingService {
    private final EncryptDataService encryptDataService;

    public boolean dataNotValid(HttpHeaders headers, String notification) {
        final String signature = Objects.requireNonNull(headers.get("twitch-eventsub-message-signature")).get(0);

        final String data = headers.get("Twitch-Eventsub-Message-Id").get(0) +
                headers.get("Twitch-Eventsub-Message-Timestamp").get(0) + notification;

        final String encryptValue = "sha256=" + encryptDataService.encryptString(data, true);
        log.debug("Received Signature: " + signature);
        log.debug("Encrypt Value: " + encryptValue);

        return !encryptValue.equals(signature);
    }

    public boolean isChallenge(StreamNotifyRequestBody.Body body) {
        return body.getChallenge() != null &&
                body.getSubscription().getStatus().equals("webhook_callback_verification_pending");
    }
}
