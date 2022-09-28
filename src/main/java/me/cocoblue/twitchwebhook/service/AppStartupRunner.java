package me.cocoblue.twitchwebhook.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.service.twitch.TwitchScheduledService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class AppStartupRunner implements CommandLineRunner {
    private final TwitchScheduledService scheduledService;

    @Override
    public void run(String[] args) {
        log.info("Start Event Subscription Checking");
        scheduledService.eventSubscriptionCheck();
    }
}