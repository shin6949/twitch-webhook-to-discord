package me.cocoblue.twitchwebhook.controller;

import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.service.*;
import me.cocoblue.twitchwebhook.vo.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "/webhook")
@Log4j2
public class WebHookPostController {
    private final UserChangeNotifyServiceImpl userChangeNotifyService;
    private final StreamNotifyServiceImpl streamNotifyService;
    private final EncrypteDataServiceImpl encrypteDataService;
    private final LogService logService;

    public WebHookPostController(UserChangeNotifyServiceImpl userChangeNotifyService, StreamNotifyServiceImpl streamNotifyService,
                                 EncrypteDataServiceImpl encrypteDataService, LogService logService) {
        this.userChangeNotifyService = userChangeNotifyService;
        this.streamNotifyService = streamNotifyService;
        this.encrypteDataService = encrypteDataService;
        this.logService = logService;
    }

    @PostMapping(path = "/stream/{broadcasterId}")
    public String receiveStreamNotification(@PathVariable String broadcasterId,
                                            @RequestBody String notification,
                                            @RequestHeader HashMap<String, String> header) throws JsonProcessingException {
        // 요청이 유효한지 체크
        String signatureFromTwitch = header.get("x-hub-signature");
        if(dataNotValid(notification, signatureFromTwitch)) {
            return "success";
        }

        // RequestBody를 Vo에 Mapping
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Map<String, Object> map = mapper.readValue(notification, Map.class);
        StreamNotification streamNotification = mapper.convertValue(map, StreamNotification.class);

        if(streamNotification.getNotification().size() == 0) {
            streamNotifyService.sendEndMessage(broadcasterId);
            return "success";
        }

        TwitchStreamNotification twitchStreamNotification = streamNotification.getNotification().get(0);
        log.info(twitchStreamNotification);
        if(logService.isAlreadySend(twitchStreamNotification.getId())) {
            return "success";
        }

        // Message Send
        streamNotifyService.sendStartMessage(twitchStreamNotification);

        // Log Insert
        streamNotifyService.insertLog(twitchStreamNotification);

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
        if(dataNotValid(notification, signatureFromTwitch)) {
            return "success";
        }

        // RequestBody를 Vo에 Mapping
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map = mapper.readValue(notification, Map.class);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        UserChangeNotifications userChangeNotifications = mapper.convertValue(map, UserChangeNotifications.class);

        // Message Send
        TwitchUserChangeNotification twitchUserChangeNotification = userChangeNotifications.getNotifications().get(0);

        // 요청이 번지수 잘못 찾아 온 경우 넘기기
        if(!twitchUserChangeNotification.getId().equals(broadcasterId)) {
            return "success";
        }

        userChangeNotifyService.sendDiscordWebHook(twitchUserChangeNotification);

        return "success";
    }

    private boolean dataNotValid(String data, String signature) {
        String encryptValue = "sha256=" + encrypteDataService.encrypteString(data);
        log.info("Received Signature: " + signature);
        log.info("Encrypt Value: " + encryptValue);

        return !encryptValue.equals(signature);
    }
}
