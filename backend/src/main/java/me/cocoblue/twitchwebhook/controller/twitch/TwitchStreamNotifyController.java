package me.cocoblue.twitchwebhook.controller.twitch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.domain.twitch.NotificationLogEntity;
import me.cocoblue.twitchwebhook.dto.twitch.Channel;
import me.cocoblue.twitchwebhook.dto.twitch.eventsub.StreamNotifyRequest;
import me.cocoblue.twitchwebhook.service.twitch.ChannelInfoService;
import me.cocoblue.twitchwebhook.service.twitch.ControllerProcessingService;
import me.cocoblue.twitchwebhook.service.twitch.StreamNotifyService;
import me.cocoblue.twitchwebhook.service.twitch.TwitchNotificationLogService;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(path = "/webhook/twitch")
@Log4j2
@AllArgsConstructor
public class TwitchStreamNotifyController {
    private final StreamNotifyService streamNotifyService;
    private final TwitchNotificationLogService twitchNotificationLogService;
    private final ChannelInfoService channelInfoService;
    private final ControllerProcessingService controllerProcessingService;

    @PostMapping(path = "/stream/{broadcasterId}/{status}")
    public String receiveStreamNotification(@PathVariable String broadcasterId, @PathVariable String status,
                                            @RequestBody String notification,
                                            @RequestHeader HttpHeaders headers) {
        log.info("Stream " + status.toUpperCase() + " Event Received");
        log.info("Received BroadcasterId: " + broadcasterId);
        log.debug("Header: " + headers.toString());
        log.debug("Body: " + notification);

        // 요청이 유효한지 체크
        if (controllerProcessingService.dataNotValid(headers, notification)) {
            log.warn("This req is NOT valid. (Encryption Value is not match between both side.) Stop the Processing.");
            return "success";
        }

        // RequestBody를 Vo에 Mapping
        final Optional<StreamNotifyRequest.Body> streamNotificationOptional = toDto(notification);

        // StreamNotifyRequest.Body가 null인 경우 처리
        if (streamNotificationOptional.isEmpty()) {
            log.warn("Failed to parse notification body.");
            return "success";
        }

        final StreamNotifyRequest.Body streamNotification = streamNotificationOptional.get();

        // Challenge 요구 시, 반응
        if (controllerProcessingService.isChallenge(streamNotification)) {
            log.info("This req is Challenge. Return the code");
            return streamNotification.getChallenge();
        }

        // 옳은 broadcasterId를 제시했는지 판단
        if (!streamNotification.getEvent().getBroadcasterUserId().equals(broadcasterId)) {
            log.warn("It doesn't match between paths broadcaster id and event broadcaster id. So, This req is invalid.");
            return "success";
        }

        if (twitchNotificationLogService.isAlreadySend(headers.getFirst("twitch-eventsub-message-id"))) {
            log.info("This req is already sent. Stop the Processing.");
            return "success";
        }

        // Log Insert
        final NotificationLogEntity notificationLogEntity = twitchNotificationLogService.insertLog(streamNotification.toCommonEvent(), headers);

        log.info("This req is valid!");
        final Channel channel = channelInfoService.getChannelInformationByBroadcasterId(streamNotification.getEvent().getBroadcasterUserId());

        // Message Send (Async)
        streamNotifyService.sendMessage(streamNotification, channel, notificationLogEntity);

        return "success";
    }

    private Optional<StreamNotifyRequest.Body> toDto(String original) {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            final Map<String, Object> map = objectMapper.readValue(original, new TypeReference<Map<String, Object>>() {});
            log.debug("map: " + map.toString());
            return Optional.ofNullable(objectMapper.convertValue(map, StreamNotifyRequest.Body.class));
        } catch (JsonProcessingException e) {
            log.error("Error converting JSON to StreamNotifyRequest.Body", e);
            return Optional.empty();
        }
    }

}