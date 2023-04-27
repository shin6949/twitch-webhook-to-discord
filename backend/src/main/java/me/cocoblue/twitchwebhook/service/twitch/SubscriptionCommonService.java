package me.cocoblue.twitchwebhook.service.twitch;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.domain.SubscriptionFormEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class SubscriptionCommonService {
    private final TwitchUserLogService twitchUserLogService;

    public List<SubscriptionFormEntity> filter(List<SubscriptionFormEntity> notifyForms, String broadcasterUserId) {
        return notifyForms.stream()
                .filter(notifyForm -> twitchUserLogService.isNotInInterval(broadcasterUserId, notifyForm.getTwitchSubscriptionType(), notifyForm.getIntervalMinute()))
                .collect(Collectors.toList());
    }
}
