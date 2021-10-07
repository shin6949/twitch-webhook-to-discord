package me.cocoblue.twitchwebhook.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@Component
@RequiredArgsConstructor
public class ScheduledService {
    private final FormService formService;

    @Scheduled(cron = "0 0 */1 * * *")
    public void dbConnection() {
        log.info("DB Connection Start");
        formService.getAllBroadcasterId();
    }
}
