package me.cocoblue.twitchwebhook.controller.twitch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.data.TwitchSubscriptionType;
import me.cocoblue.twitchwebhook.domain.NotificationLogEntity;
import me.cocoblue.twitchwebhook.dto.twitch.eventsub.ChannelUpdateRequest;
import me.cocoblue.twitchwebhook.service.twitch.ChannelNotifyService;
import me.cocoblue.twitchwebhook.service.twitch.ControllerProcessingService;
import me.cocoblue.twitchwebhook.service.twitch.TwitchNotificationLogService;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(path = "/webhook/twitch")
@Log4j2
@AllArgsConstructor
public class TwitchChannelNotifyController {
    private final ChannelNotifyService channelNotifyService;
    private final TwitchNotificationLogService twitchNotificationLogService;
    private final ControllerProcessingService controllerProcessingService;

    @PostMapping(path = "/channel/{broadcasterId}/update")
    public String receiveChannelUpdateNotification(@PathVariable String broadcasterId, @RequestBody String notification,
                                                  @RequestHeader HttpHeaders headers) {
        log.info("Channel Update Event Received");
        log.info("Received BroadcasterId: " + broadcasterId);
        log.debug("Header: " + headers.toString());
        log.debug("Body: " + notification);

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

        if (twitchNotificationLogService.isAlreadySend(headers.get("twitch-eventsub-message-id").get(0))) {
            return "success";
        }

        final NotificationLogEntity notificationLogEntity = twitchNotificationLogService.insertLog(channelUpdateNotification.toCommonEvent(), headers);

        // Message Send (Async)
        channelNotifyService.sendChannelUpdateMessage(channelUpdateNotification, notificationLogEntity);

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
