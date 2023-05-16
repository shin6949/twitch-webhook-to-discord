package me.cocoblue.twitchwebhook.dto.twitch.eventsub;

public interface NotificationEvent {
    String getBroadcasterUserId();
    String getBroadcasterUserLogin();
    String getBroadcasterUserName();
}
