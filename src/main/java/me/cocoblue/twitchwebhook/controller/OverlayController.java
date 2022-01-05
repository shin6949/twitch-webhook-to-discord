package me.cocoblue.twitchwebhook.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "/")
@Log4j2
public class OverlayController {
    // 410
    @RequestMapping("/clock")
    public String getGreenSplatoonClock() {
        log.info("410 Clock Accessed");
        return "splatoon_2_style_clock";
    }

    // COCOBLUE
    @RequestMapping("/cocoblue/clock")
    public String getOrangeSplatoonClock() {
        log.info("COCOBLUE Clock Accessed");
        return "cocoblue_splatoon_clock";
    }

    // pineee
    @RequestMapping("/pineee/clock")
    public String getPurpleSplatoonClock() {
        log.info("Pineee Clock Accessed");
        return "pineee_splatoon_clock";
    }
}