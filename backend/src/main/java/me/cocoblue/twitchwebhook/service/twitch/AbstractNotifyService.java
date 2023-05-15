package me.cocoblue.twitchwebhook.service.twitch;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.domain.discord.SubscriptionFormEntity;
import me.cocoblue.twitchwebhook.domain.push.PushSubscriptionFormEntity;
import me.cocoblue.twitchwebhook.domain.twitch.FormEntity;
import me.cocoblue.twitchwebhook.domain.twitch.NotificationLogEntity;
import me.cocoblue.twitchwebhook.dto.discord.DiscordEmbed;
import me.cocoblue.twitchwebhook.dto.twitch.Channel;
import me.cocoblue.twitchwebhook.dto.twitch.Game;
import me.cocoblue.twitchwebhook.dto.twitch.User;
import me.cocoblue.twitchwebhook.service.FirebaseInitializer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
public abstract class AbstractNotifyService {
    private final FirebaseInitializer firebaseInitializer;
    private final TwitchUserLogService twitchUserLogService;
    private FirebaseMessaging fcm = FirebaseMessaging.getInstance();

    @Async
    void sendPushMessage(final String title, final String body, final String registrationToken,
                         final long pushSubscriptionFormId, final Long notificationLogId) {
        if(FirebaseApp.getApps() == null || FirebaseApp.getApps().isEmpty()) {
            firebaseInitializer.init();
            fcm = FirebaseMessaging.getInstance();
        }

        final Message msg = Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .setToken(registrationToken)
                .build();

        try {
            fcm.send(msg);
        } catch (FirebaseMessagingException firebaseMessagingException) {
            insertPushUserLog(pushSubscriptionFormId, notificationLogId, false);
            return;
        }

        if (notificationLogId == null) {
            log.info("notificationLogEntity is NULL");
            return;
        }

        insertPushUserLog(pushSubscriptionFormId, notificationLogId, true);
    }

    void insertPushUserLog(final long pushSubscriptionFormId, final Long notificationLogId, boolean status) {
        if (notificationLogId == null) {
            log.info("notificationLogEntity is NULL");
            return;
        }

        twitchUserLogService.insertPushUserLog(pushSubscriptionFormId, notificationLogId, status);
    }

    public HttpStatus sendDiscordWebhook(DiscordEmbed.Webhook discordWebhookMessage, String webhookUrl) {
        final HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        final HttpEntity<DiscordEmbed.Webhook> entity = new HttpEntity<>(discordWebhookMessage, headers);

        final RestTemplate rt = new RestTemplate();
        return rt.exchange(webhookUrl, HttpMethod.POST, entity, String.class).getStatusCode();
    }

    protected abstract List<SubscriptionFormEntity> getNotifyForms(long broadcasterId, String subscriptionType);
    protected abstract List<PushSubscriptionFormEntity> getPushNotifyForms(long broadcasterId, String subscriptionType);
    protected abstract User getUserInfoByBroadcasterIdFromTwitch(String broadcasterUserId);

    protected abstract Game getGameInfoByIdFromTwitch(String id);

    public List<? extends FormEntity> filter(List<? extends FormEntity> notifyForms, String broadcasterUserId) {
        return notifyForms.stream()
                .filter(notifyForm -> twitchUserLogService.isNotInInterval(broadcasterUserId, notifyForm.getTwitchSubscriptionType(), notifyForm.getIntervalMinute()))
                .collect(Collectors.toList());
    }
}
