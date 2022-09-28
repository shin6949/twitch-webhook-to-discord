package me.cocoblue.twitchwebhook.controller.youtube;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/webhook/youtube")
@Log4j2
@AllArgsConstructor
public class YouTubeStreamNotifyController {
    @GetMapping("/{channelId}")
    public String challengeControl(@PathVariable String channelId,
                                 @RequestParam(name = "hub.challenge") String challengeWord) {
        log.info("Received Challenge. Returning the code.");

        return challengeWord;
    }

    @PostMapping("/{channelId}")
    public String receiveNotification(@PathVariable String channelId) {
        // TODO: Need To Process
        return "true";
    }
}
