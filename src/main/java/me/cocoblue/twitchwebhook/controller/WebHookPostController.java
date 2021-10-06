package me.cocoblue.twitchwebhook.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.service.EncryptDataService;
import me.cocoblue.twitchwebhook.service.NotifyLogService;
import me.cocoblue.twitchwebhook.service.StreamNotifyService;
import me.cocoblue.twitchwebhook.service.twitch.ChannelInfoService;
import me.cocoblue.twitchwebhook.vo.twitch.Channel;
import me.cocoblue.twitchwebhook.vo.twitch.eventsub.Body;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping(path = "/webhook")
@Log4j2
@AllArgsConstructor
public class WebHookPostController {
    private final StreamNotifyService streamNotifyService;
    private final EncryptDataService encryptDataService;
    private final NotifyLogService notifyLogService;
    private final ChannelInfoService channelInfoService;

    @PostMapping(path = "/stream/{broadcasterId}/online")
    public String receiveStreamOnlineNotification(@PathVariable String broadcasterId, @RequestBody String notification,
                                            @RequestHeader HttpHeaders headers) {
        // 요청이 유효한지 체크
        if(dataNotValid(headers, notification)) {
            return "success";
        }

        // RequestBody를 Vo에 Mapping
        final Body streamNotification = toDto(notification);

        // Challenge 요구 시, 반응
        assert streamNotification != null;
        if(isChallenge(streamNotification)) {
            return streamNotification.getChallenge();
        }

        if (notifyLogService.isAlreadySend(streamNotification.getEvent().getId())) {
            return "success";
        }

        final Channel channel = channelInfoService.getChannelInformationByBroadcasterId(streamNotification.getEvent().getBroadcasterUserId());

        // Message Send (Async)
        streamNotifyService.sendStartMessage(streamNotification.getEvent(), channel);

        // Log Insert (Async)
        streamNotifyService.insertLog(streamNotification.getEvent(), channel);

        return "success";
    }

    @PostMapping(path = "/stream/{broadcasterId}/offline")
    public String receiveStreamOfflineNotification(@PathVariable String broadcasterId, @RequestBody String notification,
                                            @RequestHeader HttpHeaders headers) {
        log.info("Offline Header: " + headers.toString());
        log.info("Offline Body: " + notification);

        // 요청이 유효한지 체크
        if(dataNotValid(headers, notification)) {
            return "success";
        }

        // RequestBody를 Vo에 Mapping
        final Body streamNotification = toDto(notification);

        // Challenge 요구 시, 반응
        assert streamNotification != null;
        if(isChallenge(streamNotification)) {
            return streamNotification.getChallenge();
        }

        // Message Send (Async)
        streamNotifyService.sendEndMessage(streamNotification.getEvent().getBroadcasterUserId());

        return "success";
    }

    private boolean dataNotValid(HttpHeaders headers, String notification) {
        final String signature = Objects.requireNonNull(headers.get("twitch-eventsub-message-signature")).get(0);

        final String data = headers.get("Twitch-Eventsub-Message-Id").get(0) +
                headers.get("Twitch-Eventsub-Message-Timestamp").get(0) + notification;

        String encryptValue = "sha256=" + encryptDataService.encryptString(data);
        log.info("Received Signature: " + signature);
        log.info("Encrypt Value: " + encryptValue);

        return !encryptValue.equals(signature);
    }

    private Body toDto(String original) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            Map<String, Object> map = mapper.readValue(original, Map.class);

            return mapper.convertValue(map, Body.class);

        } catch (JsonProcessingException jsonProcessingException) {
            jsonProcessingException.printStackTrace();
            return null;
        }
    }

    private boolean isChallenge(Body body) {
        return body.getChallenge() != null &&
                body.getSubscription().getStatus().equals("webhook_callback_verification_pending");
    }
}
