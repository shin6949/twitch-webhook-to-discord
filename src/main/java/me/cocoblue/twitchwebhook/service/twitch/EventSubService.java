package me.cocoblue.twitchwebhook.service.twitch;

import me.cocoblue.twitchwebhook.dto.twitch.eventsub.SubscriptionResponse;
import me.cocoblue.twitchwebhook.entity.StreamNotifyFormEntity;


public interface EventSubService {
    void addEventSubToTwitch(StreamNotifyFormEntity streamNotifyFormEntity);
    SubscriptionResponse getSubscriptionListFromTwitch();
}
