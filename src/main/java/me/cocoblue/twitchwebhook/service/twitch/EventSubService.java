package me.cocoblue.twitchwebhook.service.twitch;

import me.cocoblue.twitchwebhook.dto.twitch.eventsub.SubscriptionResponse;
import me.cocoblue.twitchwebhook.entity.SubscriptionFormEntity;


public interface EventSubService {
    void addEventSubToTwitch(SubscriptionFormEntity subscriptionFormEntity, String AccessToken);
    SubscriptionResponse getSubscriptionListFromTwitch(String accessToken);
}
