package me.cocoblue.twitchwebhook.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.service.EncryptDataService;
import me.cocoblue.twitchwebhook.service.NotifyLogService;
import me.cocoblue.twitchwebhook.service.StreamNotifyService;
import me.cocoblue.twitchwebhook.service.UserChangeNotifyService;
import me.cocoblue.twitchwebhook.vo.FollowNotifications;
import me.cocoblue.twitchwebhook.vo.StreamNotification;
import me.cocoblue.twitchwebhook.vo.UserChangeNotifications;
import me.cocoblue.twitchwebhook.vo.twitch.notification.Stream;
import me.cocoblue.twitchwebhook.vo.twitch.notification.UserChange;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "/webhook")
@Log4j2
@AllArgsConstructor
public class WebHookPostController {
    private final UserChangeNotifyService userChangeNotifyService;
    private final StreamNotifyService streamNotifyService;
    private final EncryptDataService encryptDataService;
    private final NotifyLogService notifyLogService;

    @PostMapping(path = "/stream/{broadcasterId}")
    public String receiveStreamNotification(@PathVariable String broadcasterId,
                                            @RequestBody String notification,
                                            @RequestHeader HashMap<String, String> header) throws JsonProcessingException {
        // 요청이 유효한지 체크
        String signatureFromTwitch = header.get("x-hub-signature");
        log.info(notification);
        if (dataNotValid(notification, signatureFromTwitch)) {
            return "success";
        }

        // RequestBody를 Vo에 Mapping
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Map<String, Object> map = mapper.readValue(notification, Map.class);
        StreamNotification streamNotification = mapper.convertValue(map, StreamNotification.class);

        if (streamNotification.getNotification().size() == 0) {
            streamNotifyService.sendEndMessage(broadcasterId);
            return "success";
        }

        Stream stream = streamNotification.getNotification().get(0);
        log.info(stream);
        if (notifyLogService.isAlreadySend(stream.getId())) {
            return "success";
        }

        // Message Send
        streamNotifyService.sendStartMessage(stream);

        // Log Insert
        streamNotifyService.insertLog(stream);

        return "success";
    }

    @PostMapping(path = "/follow/from/{broadcasterId}")
    public String receiveFollowFromNotification(@PathVariable String broadcasterId,
                                                @RequestBody FollowNotifications notification) {
        log.info(broadcasterId);
        log.info(notification.toString());

        return "success";
    }

    @PostMapping(path = "/follow/to/{broadcasterId}")
    public String receiveFollowToNotification(@PathVariable String broadcasterId,
                                              @RequestBody FollowNotifications notification) {
        log.info(broadcasterId);
        log.info(notification.toString());

        return "success";
    }

    @PostMapping(path = "/user/{broadcasterId}")
    public String receiveUserChangeNotification(@PathVariable String broadcasterId,
                                                @RequestBody String notification,
                                                @RequestHeader HashMap<String, String> header) throws JsonProcessingException {

        // 요청이 유효한지 체크
        String signatureFromTwitch = header.get("x-hub-signature");
        if (dataNotValid(notification, signatureFromTwitch)) {
            return "success";
        }

        // RequestBody를 Vo에 Mapping
        ObjectMapper mapper = new ObjectMapper();
        Map map = mapper.readValue(notification, Map.class);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        UserChangeNotifications userChangeNotifications = mapper.convertValue(map, UserChangeNotifications.class);

        // Message Send
        UserChange userChange = userChangeNotifications.getNotifications().get(0);

        // 요청이 번지수 잘못 찾아 온 경우 넘기기
        if (!userChange.getId().equals(broadcasterId)) {
            return "success";
        }

        userChangeNotifyService.sendDiscordWebHook(userChange);

        return "success";
    }

    private boolean dataNotValid(String data, String signature) {
        String encryptValue = "sha256=" + encryptDataService.encryptString(data);
        log.info("Received Signature: " + signature);
        log.info("Encrypt Value: " + encryptValue);

        return !encryptValue.equals(signature);
    }
}
