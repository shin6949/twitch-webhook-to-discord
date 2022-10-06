package me.cocoblue.twitchwebhook.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.service.twitch.TwitchScheduledService;
import me.cocoblue.twitchwebhook.service.youtube.YouTubeScheduledService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class AppStartupRunner implements CommandLineRunner {
    private final TwitchScheduledService twitchScheduledService;
    private final YouTubeScheduledService youTubeScheduledService;

    @Override
    public void run(String[] args) {
        log.info("Start Event Subscription Checking");

        log.info("Twitch Subscription Checking Start");
        twitchScheduledService.eventSubscriptionCheck();
        log.info("Twitch Subscription Checking Finished");

        log.info("Youtube Subscription Checking Start");
        youTubeScheduledService.youtubeAllSubscriptionCheck();
        log.info("Youtube Subscription Checking Finished");
    }
}