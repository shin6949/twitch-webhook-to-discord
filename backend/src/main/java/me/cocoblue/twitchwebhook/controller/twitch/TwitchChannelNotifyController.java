package me.cocoblue.twitchwebhook.controller.twitch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.domain.twitch.NotificationLogEntity;
import me.cocoblue.twitchwebhook.dto.twitch.eventsub.ChannelUpdateRequestBody;
import me.cocoblue.twitchwebhook.service.twitch.ChannelNotifyService;
import me.cocoblue.twitchwebhook.service.twitch.ControllerProcessingService;
import me.cocoblue.twitchwebhook.service.twitch.TwitchNotificationLogService;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

@AllArgsConstructor
@RestController
@RequestMapping(path = "/webhook/twitch")
@Log4j2
public class TwitchChannelNotifyController {
    private final ChannelNotifyService channelNotifyService;
    private final TwitchNotificationLogService twitchNotificationLogService;
    private final ControllerProcessingService controllerProcessingService;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @PostMapping(path = "/channel/{broadcasterId}/update")
    public String receiveChannelUpdateNotification(@PathVariable final String broadcasterId, @RequestBody final String notification,
            @RequestHeader final HttpHeaders headers) {
        log.info("Channel Update Event Received");
        log.info("Received BroadcasterId: " + broadcasterId);
        log.debug("Header: " + headers.toString());
        log.debug("Body String: " + notification);

        if (controllerProcessingService.dataNotValid(headers, notification)) {
            return "success";
        }

        final ChannelUpdateRequestBody.Body channelUpdateNotification = toChannelUpdateRequest(notification);
        log.debug("Body: " + channelUpdateNotification);
        if (channelUpdateNotification != null) {
            if (channelUpdateNotification.getChallenge() != null &&
                    "webhook_callback_verification_pending".equals(
                            channelUpdateNotification.getSubscription().getStatus())) {
                return channelUpdateNotification.getChallenge();
            }

            final String messageID = Objects.requireNonNull(headers.get("twitch-eventsub-message-id")).get(0);
            if (twitchNotificationLogService.isAlreadySend(messageID)) {
                return "success";
            }

            final NotificationLogEntity notificationLogEntity =
                    twitchNotificationLogService.insertLog(channelUpdateNotification, headers);
            channelNotifyService.sendMessage(channelUpdateNotification, notificationLogEntity, null);
        }

        return "success";
    }

    private ChannelUpdateRequestBody.Body toChannelUpdateRequest(final String original) {
        try {
            final Map<String, Object> map = objectMapper.readValue(original, new TypeReference<Map<String, Object>>() {});
            return objectMapper.convertValue(map, ChannelUpdateRequestBody.Body.class);
        } catch (JsonProcessingException e) {
            log.error("An error occurred while deserializing JSON.", e);
            return null;
        }
    }
}
