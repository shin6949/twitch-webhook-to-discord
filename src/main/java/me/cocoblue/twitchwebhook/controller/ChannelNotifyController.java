package me.cocoblue.twitchwebhook.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.dto.twitch.Channel;
import me.cocoblue.twitchwebhook.dto.twitch.eventsub.ChannelUpdateRequest;
import me.cocoblue.twitchwebhook.dto.twitch.eventsub.StreamNotifyRequest;
import me.cocoblue.twitchwebhook.service.ChannelNotifyService;
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
public class ChannelNotifyController {
    private final ChannelNotifyService channelNotifyService;
    private final NotifyLogService notifyLogService;
    private final ChannelInfoService channelInfoService;
    private final ControllerProcessingService controllerProcessingService;

    @PostMapping(path = "/channel/{broadcasterId}/update")
    public String receiveChannelUpdateNotification(@PathVariable String broadcasterId, @RequestBody String notification,
                                                  @RequestHeader HttpHeaders headers) {
        // 요청이 유효한지 체크
        if(controllerProcessingService.dataNotValid(headers, notification)) {
            return "success";
        }

        // RequestBody를 Vo에 Mapping
        final ChannelUpdateRequest.Body channelUpdateNotification = toDto(notification);

        // Challenge 요구 시, 반응
        assert channelUpdateNotification != null;
        if(channelUpdateNotification.getChallenge() != null &&
                channelUpdateNotification.getSubscription().getStatus().equals("webhook_callback_verification_pending")) {
            return channelUpdateNotification.getChallenge();
        }

        if (notifyLogService.isAlreadySend(channelUpdateNotification.getEvent().getId())) {
            return "success";
        }

        // Message Send (Async)
        channelNotifyService.sendChannelUpdateMessage(channelUpdateNotification);

        // Log Insert (Async)
        notifyLogService.insertLog(channelUpdateNotification.toCommonEvent());

        return "success";
    }

    private ChannelUpdateRequest.Body toDto(String original) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            Map<String, Object> map = mapper.readValue(original, Map.class);

            return mapper.convertValue(map, ChannelUpdateRequest.Body.class);

        } catch (JsonProcessingException jsonProcessingException) {
            jsonProcessingException.printStackTrace();
            return null;
        }
    }
}
