package me.cocoblue.twitchwebhook.service.twitch;

import me.cocoblue.twitchwebhook.vo.twitch.eventsub.SubscriptionList;

public interface SubscriptionListService {
    SubscriptionList getSubscriptionListFromTwitch();
}
