package me.cocoblue.twitchwebhook.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "/cocoblue")
@Log4j2
public class CocoblueOverlayController {
    @RequestMapping("/clock")
    public String getGreenSplatoonClock() {
        return "cocoblue_splatoon_clock";
    }
}
