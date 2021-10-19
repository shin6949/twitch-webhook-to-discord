package me.cocoblue.twitchwebhook.service.twitch;

import me.cocoblue.twitchwebhook.dto.twitch.eventsub.SubscriptionResponse;
import me.cocoblue.twitchwebhook.entity.StreamNotifyForm;


public interface EventSubService {
    void addEventSubToTwitch(StreamNotifyForm streamNotifyForm);
    SubscriptionResponse getSubscriptionListFromTwitch();
}
