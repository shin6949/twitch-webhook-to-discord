package me.cocoblue.twitchwebhook.service.youtube;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.domain.YouTubeSubscriptionFormEntity;
import me.cocoblue.twitchwebhook.domain.YouTubeSubscriptionFormRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
@Component
@RequiredArgsConstructor
public class YouTubeScheduledService {
    @Value("${webapp.base-url}")
    private String webappBaseUrl;

    @Value("${twitch.event-renew}")
    private boolean eventEnabled;

    private final PubSubHubbubService pubSubHubbubService;

    private final YouTubeSubscriptionFormRepository youTubeSubscriptionFormRepository;

    @Scheduled(cron = "0 20 */1 * * *")
    public void youtubeSubscriptionCheck() {
        if(!eventEnabled) {
            log.info("Event Renew Function Disabled. Do Not Processing.");
            return;
        }

        log.info("YouTube Event Subscription Check Start");
        final List<YouTubeSubscriptionFormEntity> youTubeSubscriptionFormEntityList
                = youTubeSubscriptionFormRepository.findAll();
    }
}
