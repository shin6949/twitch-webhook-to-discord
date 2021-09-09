package me.cocoblue.twitchwebhook.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "/")
@Log4j2
public class OverlayController {
    @RequestMapping("/clock")
    public String getGreenSplatoonClock() {
        return "splatoon_2_style_clock";
    }
}
