package me.cocoblue.twitchwebhook.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
        // 요청이 유효한지 체크
        if(controllerProcessingService.dataNotValid(headers, notification)) {
            return "success";
        }

        // RequestBody를 Vo에 Mapping
        final StreamNotifyRequest.Body streamNotification = toDto(notification);

        // Challenge 요구 시, 반응
        assert streamNotification != null;
        if(controllerProcessingService.isChallenge(streamNotification)) {
            return streamNotification.getChallenge();
        }

        if (notifyLogService.isAlreadySend(streamNotification.getEvent().getId())) {
            return "success";
        }

        final Channel channel = channelInfoService.getChannelInformationByBroadcasterId(streamNotification.getEvent().getBroadcasterUserId());

        // Message Send (Async)
        streamNotifyService.sendMessage(streamNotification, channel);

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
        if(controllerProcessingService.dataNotValid(headers, notification)) {
            return "success";
        }

        // RequestBody를 Vo에 Mapping
        final StreamNotifyRequest.Body streamNotification = toDto(notification);

        // Challenge 요구 시, 반응
        assert streamNotification != null;
        if(controllerProcessingService.isChallenge(streamNotification)) {
            return streamNotification.getChallenge();
        }

        final Channel channel = channelInfoService.getChannelInformationByBroadcasterId(streamNotification.getEvent().getBroadcasterUserId());

        // Message Send (Async)
        streamNotifyService.sendMessage(streamNotification, channel);

        return "success";
    }

    private StreamNotifyRequest.Body toDto(String original) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            Map<String, Object> map = mapper.readValue(original, Map.class);

            return mapper.convertValue(map, StreamNotifyRequest.Body.class);

        } catch (JsonProcessingException jsonProcessingException) {
            jsonProcessingException.printStackTrace();
            return null;
        }
    }
}
