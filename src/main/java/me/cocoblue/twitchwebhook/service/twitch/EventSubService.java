package me.cocoblue.twitchwebhook.service.twitch;

import me.cocoblue.twitchwebhook.entity.StreamNotifyForm;
import me.cocoblue.twitchwebhook.vo.twitch.eventsub.SubscriptionList;

public interface EventSubService {
    void addEventSubToTwitch(StreamNotifyForm streamNotifyForm);
    SubscriptionList getSubscriptionListFromTwitch();
}
