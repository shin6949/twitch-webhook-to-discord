package me.cocoblue.twitchwebhook.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.domain.NotificationLogEntity;
import me.cocoblue.twitchwebhook.dto.twitch.Channel;
import me.cocoblue.twitchwebhook.dto.twitch.eventsub.StreamNotifyRequest;
import me.cocoblue.twitchwebhook.service.ControllerProcessingService;
import me.cocoblue.twitchwebhook.service.NotifyLogService;
import me.cocoblue.twitchwebhook.service.StreamNotifyService;
import me.cocoblue.twitchwebhook.service.twitch.ChannelInfoService;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(path = "/webhook")
@Log4j2
@AllArgsConstructor
public class StreamNotifyController {
    private final StreamNotifyService streamNotifyService;
    private final NotifyLogService notifyLogService;
    private final ChannelInfoService channelInfoService;
    private final ControllerProcessingService controllerProcessingService;

    @PostMapping(path = "/stream/{broadcasterId}/online")
    public String receiveStreamOnlineNotification(@PathVariable String broadcasterId, @RequestBody String notification,
                                            @RequestHeader HttpHeaders headers) {
        log.info("Stream Online Event Received");
        log.info("Received BroadcasterId: " + broadcasterId);
        log.debug("Header: " + headers.toString());
        log.debug("Body: " + notification);

        // 요청이 유효한지 체크
        if(controllerProcessingService.dataNotValid(headers, notification)) {
            log.warn("This req is NOT valid. (Encryption Value is not match between both side.) Stop the Processing.");
            return "success";
        }

        // RequestBody를 Vo에 Mapping
        final StreamNotifyRequest.Body streamNotification = toDto(notification);

        // Challenge 요구 시, 반응
        assert streamNotification != null;
        if(controllerProcessingService.isChallenge(streamNotification)) {
            log.info("This req is Challenge. Return the code");
            return streamNotification.getChallenge();
        }

        // 옳은 broadcasterId를 제시했는지 판단
        if(!streamNotification.getEvent().getBroadcasterUserId().equals(broadcasterId)) {
            log.warn("It doesn't match between paths broadcaster id and event broadcaster id. So, This req is invalid.");
            return "success";
        }

        // 이미 전송한 알림인지 파악
        if (notifyLogService.isAlreadySend(headers.get("twitch-eventsub-message-id").get(0))) {
            log.info("This req is already sent. Stop the Processing.");
            return "success";
        }

        log.info("This req is valid!");
        final Channel channel = channelInfoService.getChannelInformationByBroadcasterId(streamNotification.getEvent().getBroadcasterUserId());

        // Message Send (Async)
        streamNotifyService.sendMessage(streamNotification, channel, null);

        // Log Insert (Async)
        notifyLogService.insertLog(streamNotification.toCommonEvent(), headers);

        return "success";
    }

    @PostMapping(path = "/stream/{broadcasterId}/offline")
    public String receiveStreamOfflineNotification(@PathVariable String broadcasterId, @RequestBody String notification,
                                            @RequestHeader HttpHeaders headers) {
        log.info("Stream Offline Event Received");
        log.info("Received BroadcasterId: " + broadcasterId);
        log.debug("Header: " + headers.toString());
        log.debug("Body: " + notification);

        // 요청이 유효한지 체크
        if(controllerProcessingService.dataNotValid(headers, notification)) {
            log.warn("This req is NOT valid. (Encryption Value is not match between both side.) Stop the Processing.");
            return "success";
        }

        // RequestBody를 Vo에 Mapping
        final StreamNotifyRequest.Body streamNotification = toDto(notification);

        // Challenge 요구 시, 반응
        assert streamNotification != null;
        if(controllerProcessingService.isChallenge(streamNotification)) {
            log.info("This req is Challenge. Return the code");
            return streamNotification.getChallenge();
        }

        // 옳은 broadcasterId를 제시했는지 판단
        if(!streamNotification.getEvent().getBroadcasterUserId().equals(broadcasterId)) {
            log.warn("It doesn't match between paths broadcaster id and event broadcaster id. So, This req is invalid.");
            return "success";
        }

        if (notifyLogService.isAlreadySend(headers.get("twitch-eventsub-message-id").get(0))) {
            log.info("This req is already sent. Stop the Processing.");
            return "success";
        }

        log.info("This req is valid!");
        final Channel channel = channelInfoService.getChannelInformationByBroadcasterId(streamNotification.getEvent().getBroadcasterUserId());

        // Log Insert
        final NotificationLogEntity notificationLogEntity = notifyLogService.insertLog(streamNotification.toCommonEvent(), headers);

        // Message Send (Async)
        streamNotifyService.sendMessage(streamNotification, channel, notificationLogEntity);

        return "success";
    }

    private StreamNotifyRequest.Body toDto(String original) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            Map<String, Object> map = mapper.readValue(original, Map.class);

            log.debug("map: " + map.toString());
            return mapper.convertValue(map, StreamNotifyRequest.Body.class);

        } catch (JsonProcessingException jsonProcessingException) {
            jsonProcessingException.printStackTrace();
            return null;
        }
    }
}
