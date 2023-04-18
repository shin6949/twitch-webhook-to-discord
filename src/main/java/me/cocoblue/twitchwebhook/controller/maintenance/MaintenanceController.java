package me.cocoblue.twitchwebhook.controller.maintenance;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class MaintenanceController {
    @RequestMapping("")
    public String getMain() {
        return "Success";
    }
}
