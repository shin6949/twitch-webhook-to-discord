package me.cocoblue.twitchwebhook.service.twitch;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.data.TwitchSubscriptionType;
import me.cocoblue.twitchwebhook.domain.discord.SubscriptionFormEntity;
import me.cocoblue.twitchwebhook.domain.twitch.FormEntity;
import me.cocoblue.twitchwebhook.domain.twitch.NotificationLogEntity;
import me.cocoblue.twitchwebhook.domain.twitch.PushSubscriptionFormEntity;
import me.cocoblue.twitchwebhook.dto.discord.DiscordEmbed;
import me.cocoblue.twitchwebhook.dto.twitch.Channel;
import me.cocoblue.twitchwebhook.dto.twitch.Game;
import me.cocoblue.twitchwebhook.dto.twitch.User;
import me.cocoblue.twitchwebhook.dto.twitch.eventsub.EventNotificationRequestBody;
import me.cocoblue.twitchwebhook.dto.twitch.eventsub.NotificationEvent;
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
    private final EventSubService eventSubService;
    private final UserInfoService userInfoService;
    private final GameInfoService gameInfoService;
    private final NotificationFormService notificationFormService;
    private FirebaseMessaging fcm = FirebaseMessaging.getInstance();

    @Async
    void sendPushMessage(final Message msg, final long pushSubscriptionFormId, final Long notificationLogId) {
        if(FirebaseApp.getApps() == null || FirebaseApp.getApps().isEmpty()) {
            firebaseInitializer.init();
            fcm = FirebaseMessaging.getInstance();
        }

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

    protected abstract DiscordEmbed.Webhook makeDiscordEmbed(final NotificationEvent event, final SubscriptionFormEntity form,
                                                             final Channel channel, final User twitchUser, final Game game);
    protected abstract Message makePushMessage(final User twitchUser, final Channel channel, final Game game,
                                               final PushSubscriptionFormEntity form, final NotificationEvent event);

    protected abstract String getGameId(final NotificationEvent event, final Channel channel);

    public <T extends EventNotificationRequestBody.Body> void sendMessage(final T body, final NotificationLogEntity notificationLogEntity,
                                                                          final Channel channel) {
        log.info("Stream Notify Service Called");
        log.debug("Received Body: " + body.toString());

        final long broadcasterId = Long.parseLong(body.getEvent().getBroadcasterUserId());
        final List<SubscriptionFormEntity> notifyForms = notificationFormService.getFormByBroadcasterIdAndType(broadcasterId, body.getSubscription().getType());
        log.debug("Received Notify Forms: " + notifyForms);

        final List<PushSubscriptionFormEntity> pushNotifyForms = notificationFormService.getPushFormByBroadcasterIdAndType(broadcasterId, body.getSubscription().getType());
        log.debug("Received pushNotifyForms Forms: " + pushNotifyForms);

        if(notifyForms.isEmpty() && pushNotifyForms.isEmpty()) {
            log.info("filteredNotifyForms and pushNotifyForms are empty. Delete the Subscription");

            eventSubService.deleteEventSub(body.getSubscription().getId());
            return;
        }

        final List<SubscriptionFormEntity> filteredNotifyForms = filter(notifyForms, body.getEvent().getBroadcasterUserId())
                .stream()
                .map(formEntity -> (SubscriptionFormEntity) formEntity)
                .collect(Collectors.toList());

        final List<PushSubscriptionFormEntity> filteredPushNotifyForms = filter(pushNotifyForms, body.getEvent().getBroadcasterUserId())
                .stream()
                .map(formEntity -> (PushSubscriptionFormEntity) formEntity)
                .collect(Collectors.toList());

        if(filteredNotifyForms.isEmpty() && filteredPushNotifyForms.isEmpty()) {
            log.info("Filtered filteredNotifyForms and filteredPushNotifyForms are empty. finish The Processing");
            return;
        }

        final User twitchUser = userInfoService.getUserInfoByBroadcasterIdFromTwitch(body.getEvent().getBroadcasterUserId()).get();
        log.debug("Got User Info From Twitch: " + twitchUser);

        // Game 정보 얻어오기
        // STREAM_OFFLINE 시에는 필요 없으므로 null 처리
        final Game game = body.toNotificationLogEntity().getTwitchSubscriptionType() != TwitchSubscriptionType.STREAM_OFFLINE ?
                gameInfoService.getGameInfoByIdFromTwitch(getGameId(body.getEvent(), channel)) :
                null;

        filteredNotifyForms.parallelStream().forEach(notifyForm -> {
            final DiscordEmbed.Webhook discordWebhookMessage = makeDiscordEmbed(body.getEvent(), notifyForm, channel, twitchUser, game);
            log.debug("Configured Webhook Message: " + discordWebhookMessage);
            final HttpStatus httpStatus = sendDiscordWebhook(discordWebhookMessage, notifyForm.getWebhookId().getWebhookUrl());

            if(notificationLogEntity != null) {
                twitchUserLogService.insertUserLog(notifyForm, notificationLogEntity, httpStatus.is2xxSuccessful());
            }
        });

        filteredPushNotifyForms.forEach(pushNotifyForm -> {
            final Message message = makePushMessage(twitchUser, channel, game, pushNotifyForm, body.getEvent());
            if(notificationLogEntity != null) {
                sendPushMessage(message, pushNotifyForm.getId(), notificationLogEntity.getId());
            }
        });
    }

    public List<? extends FormEntity> filter(List<? extends FormEntity> notifyForms, String broadcasterUserId) {
        return notifyForms.stream()
                .filter(notifyForm -> twitchUserLogService.isNotInInterval(broadcasterUserId, notifyForm.getTwitchSubscriptionType(), notifyForm.getIntervalMinute()))
                .collect(Collectors.toList());
    }
}
