package me.cocoblue.twitchwebhook.service.twitch;

import me.cocoblue.twitchwebhook.dto.Form;
import me.cocoblue.twitchwebhook.vo.twitch.eventsub.SubscriptionResponse;

public interface EventSubService {
    void addEventSubToTwitch(Form form);
    SubscriptionResponse getSubscriptionListFromTwitch();
}
