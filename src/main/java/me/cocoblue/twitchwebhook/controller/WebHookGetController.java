package me.cocoblue.twitchwebhook.controller;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.dto.RequestLog;
import me.cocoblue.twitchwebhook.exception.IncorrectValueException;
import me.cocoblue.twitchwebhook.service.RequestLogService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/webhook")
@Log4j2
@AllArgsConstructor
public class WebHookGetController {
    private final RequestLogService requestLogService;

    @GetMapping(path = "/stream/{broadcasterId}")
    public String receiveStreamRequest(@PathVariable String broadcasterId,
                                       @RequestParam(name = "hub.challenge", required = false) String challengeCode,
                                       @RequestParam(name = "hub.mode") String mode,
                                       @RequestParam(name = "hub.topic") String topic) {

        log.info("{}", challengeCode);
        String topicUrl = "https://api.twitch.tv/helix/streams?user_id=";
        if (!topic.equals(topicUrl + broadcasterId)) {
            throw new IncorrectValueException("");
        }

        if (mode.equals("denied")) {
            log.info("DENIED Received");
            return "success";
        }

        if (mode.equals("unsubscribe")) {
            return challengeCode;
        }

        RequestLog requestLog = new RequestLog(Integer.parseInt(broadcasterId), challengeCode);
        requestLogService.insertStreamNotifyRequestLog(requestLog);

        return challengeCode;
    }

//    @GetMapping(path = "/stream/{broadcasterId}")
//    public String receiveStreamRequest(@PathVariable String broadcasterId,
//                                       @RequestParam Map<String, String> params) {
//
//        log.info("{}", params);
//
//        return "challengeCode";
//    }

    @GetMapping(path = "/follow/from/{broadcasterId}")
    public String receiveFollowFromRequest(@PathVariable String broadcasterId,
                                           @RequestParam(name = "hub.challenge") String challengeCode,
                                           @RequestParam(name = "hub.mode") String mode,
                                           @RequestParam(name = "hub.topic") String topic) {

        String topicUrl = "https://api.twitch.tv/helix/users/follows?from_id=";
        return topicCheck(broadcasterId, challengeCode, mode, topic, topicUrl);
    }

    @GetMapping(path = "/follow/to/{broadcasterId}")
    public String receiveFollowToRequest(@PathVariable String broadcasterId,
                                         @RequestParam(name = "hub.challenge") String challengeCode,
                                         @RequestParam(name = "hub.mode") String mode,
                                         @RequestParam(name = "hub.topic") String topic) {

        String topicUrl = "https://api.twitch.tv/helix/users/follows?to_id=";
        return topicCheck(broadcasterId, challengeCode, mode, topic, topicUrl);
    }

    @GetMapping(path = "/user/{broadcasterId}")
    public String receiveUserChangeRequest(@PathVariable String broadcasterId,
                                           @RequestParam(name = "hub.challenge") String challengeCode,
                                           @RequestParam(name = "hub.mode") String mode,
                                           @RequestParam(name = "hub.topic") String topic) {

        String topicUrl = "https://api.twitch.tv/helix/users?id=";
        return topicCheck(broadcasterId, challengeCode, mode, topic, topicUrl);
    }

    private String topicCheck(String broadcasterId, String challengeCode, String mode, String topic, String topicUrl) {
        if (!topic.equals(topicUrl + broadcasterId)) {
            throw new IncorrectValueException("");
        }

        if (mode.equals("unsubscribe")) {
            return challengeCode;
        }

        RequestLog requestLog = new RequestLog(Integer.parseInt(broadcasterId), challengeCode);
        requestLogService.insertStreamNotifyRequestLog(requestLog);

        return challengeCode;
    }
}
