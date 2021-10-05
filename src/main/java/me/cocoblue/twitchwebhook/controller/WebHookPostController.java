package me.cocoblue.twitchwebhook.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.service.EncryptDataService;
import me.cocoblue.twitchwebhook.service.NotifyLogService;
import me.cocoblue.twitchwebhook.service.StreamNotifyService;
import me.cocoblue.twitchwebhook.vo.twitch.eventsub.Body;
import me.cocoblue.twitchwebhook.vo.twitch.eventsub.Header;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(path = "/webhook")
@Log4j2
@AllArgsConstructor
public class WebHookPostController {
    private final StreamNotifyService streamNotifyService;
    private final EncryptDataService encryptDataService;
    private final NotifyLogService notifyLogService;

    @PostMapping(path = "/stream/{broadcasterId}/online")
    public String receiveStreamNotification(@PathVariable String broadcasterId,
                                            @RequestBody String notification,
                                            @RequestHeader Header header) throws JsonProcessingException {
        // 요청이 유효한지 체크
        log.info(notification);
        if(dataNotValid(notification, header.getSignature())) {
            return "success";
        }

        // RequestBody를 Vo에 Mapping
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Map<String, Object> map = mapper.readValue(notification, Map.class);
        Body streamNotification = mapper.convertValue(map, Body.class);

        // Challenge 요구 시, 반응
        if(streamNotification.getChallenge() != null &&
                streamNotification.getSubscription().getStatus().equals("webhook_callback_verification_pending")) {
            return streamNotification.getChallenge();
        }


//        if (streamNotification.getNotification().size() == 0) {
//            streamNotifyService.sendEndMessage(broadcasterId);
//            return "success";
//        }

        if (notifyLogService.isAlreadySend(streamNotification.getEvent().getId())) {
            return "success";
        }

        // Message Send
        streamNotifyService.sendStartMessage(stream);

        // Log Insert
        streamNotifyService.insertLog(stream);

        return "success";
    }

    private boolean dataNotValid(String data, String signature) {
        String encryptValue = "sha256=" + encryptDataService.encryptString(data);
        log.info("Received Signature: " + signature);
        log.info("Encrypt Value: " + encryptValue);

        return !encryptValue.equals(signature);
    }
}
