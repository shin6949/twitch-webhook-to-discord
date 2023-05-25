package me.cocoblue.twitchwebhook.controller.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/uuid")
public class UUIDController {
    @PostMapping("/create")
    public void createUUID() {

    }

    @PostMapping("/update")
    public void updateFcmToken() {

    }
}
