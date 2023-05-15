package me.cocoblue.twitchwebhook.service.twitch;

import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.data.LanguageIsoData;
import me.cocoblue.twitchwebhook.domain.twitch.NotificationLogEntity;
import me.cocoblue.twitchwebhook.domain.discord.SubscriptionFormEntity;
import me.cocoblue.twitchwebhook.domain.push.PushSubscriptionFormEntity;
import me.cocoblue.twitchwebhook.dto.discord.DiscordEmbed;
import me.cocoblue.twitchwebhook.dto.twitch.Game;
import me.cocoblue.twitchwebhook.dto.twitch.User;
import me.cocoblue.twitchwebhook.dto.twitch.eventsub.ChannelUpdateRequest;
import me.cocoblue.twitchwebhook.service.FirebaseInitializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@Log4j2
public class ChannelNotifyService extends AbstractNotifyService {
    @Value("${twitch.logo-url}")
    private String twitchLogoUrl;
    private final EventSubService eventSubService;
    private final NotificationFormService notificationFormService;
    private final UserInfoService userInfoService;
    private final MessageSource messageSource;
    private final GameInfoService gameInfoService;
    private final TwitchUserLogService twitchUserLogService;

    public ChannelNotifyService(FirebaseInitializer firebaseInitializer, TwitchUserLogService twitchUserLogService,
                                EventSubService eventSubService, NotificationFormService notificationFormService,
                                UserInfoService userInfoService, MessageSource messageSource,
                                GameInfoService gameInfoService) {
        super(firebaseInitializer, twitchUserLogService);
        this.eventSubService = eventSubService;
        this.notificationFormService = notificationFormService;
        this.userInfoService = userInfoService;
        this.messageSource = messageSource;
        this.gameInfoService = gameInfoService;
        this.twitchUserLogService = twitchUserLogService;
    }

    public void sendChannelUpdateMessage(final ChannelUpdateRequest.Body body, final NotificationLogEntity notificationLogEntity) {
        log.info("Send Channel Update Message");
        log.debug("Received Body: " + body);

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
                .map(formEntity -> (SubscriptionFormEntity) formEntity) // 형변환 필요
                .collect(Collectors.toList());

        final List<PushSubscriptionFormEntity> filteredPushNotifyForms = filter(pushNotifyForms, body.getEvent().getBroadcasterUserId())
                .stream()
                .map(formEntity -> (PushSubscriptionFormEntity) formEntity) // 형변환 필요
                .collect(Collectors.toList());

        if(filteredNotifyForms.isEmpty() && filteredPushNotifyForms.isEmpty()) {
            log.info("Filtered filteredNotifyForms and filteredPushNotifyForms are empty. finish The Processing");
            return;
        }

        final User twitchUser = userInfoService.getUserInfoByBroadcasterIdFromTwitch(body.getEvent().getBroadcasterUserId()).get();
        log.debug("Got User Info From Twitch: " + twitchUser);

        // Game 정보 얻어오기
        final Game game = gameInfoService.getGameInfoByIdFromTwitch(body.getEvent().getCategoryId());

        filteredNotifyForms.parallelStream().forEach(notifyForm -> {
            final DiscordEmbed.Webhook discordWebhookMessage = makeChannelUpdateDiscordWebhook(body, notifyForm, twitchUser, game);
            log.debug("Made Webhook Message: " + discordWebhookMessage);

            final HttpStatus httpStatus = sendDiscordWebhook(discordWebhookMessage, notifyForm.getWebhookId().getWebhookUrl());

            if(notificationLogEntity == null) {
                log.info("notificationLogEntity is NULL");
                return;
            }

            twitchUserLogService.insertUserLog(notifyForm, notificationLogEntity, httpStatus.is2xxSuccessful());
        });

        filteredPushNotifyForms.forEach(pushNotifyForm -> {
            final ChannelUpdateRequest.Event event = body.getEvent();
            final Locale locale = Locale.forLanguageTag(pushNotifyForm.getLanguageIsoData().getCode());
            final String messageTitle = String.format("%s(%s)%s", twitchUser.getDisplayName(), twitchUser.getLogin(),
                    messageSource.getMessage("channel.update.event-message", null, locale));
            final String messageBody = String.format("%s%s\n%s%s", messageSource.getMessage("channel.update.title", null, locale), event.getTitle(),
                    messageSource.getMessage("channel.update.game-name", null, locale), game.getName());

            assert notificationLogEntity != null;
            sendPushMessage(messageTitle, messageBody, pushNotifyForm.getRegistrationToken(), pushNotifyForm.getId(), notificationLogEntity.getId());
        });
    }

    private DiscordEmbed.Webhook makeChannelUpdateDiscordWebhook(final ChannelUpdateRequest.Body body, final SubscriptionFormEntity form,
                                                                 final User user, final Game game) {
        // 설정한 언어 받아오기
        final Locale locale = Locale.forLanguageTag(form.getLanguageIsoData().getCode());

        final ChannelUpdateRequest.Event event = body.getEvent();

        // Author Area
        final String twitchUrl = "https://twitch.tv/";
        final String authorURL = twitchUrl + event.getBroadcasterUserLogin();
        final String authorProfileURL = user.getProfileImageUrl();
        String authorName;
        if(user.getDisplayName().equals(user.getLogin())) {
            authorName = String.format("%s%s", user.getDisplayName(),
                    messageSource.getMessage("channel.update.event-message", null, locale));
            log.info("messageSource: " + messageSource.getMessage("channel.update.event-message", null, locale));
            log.info("original messageSource: " + messageSource.getMessage("channel.update.event-message", null, Locale.forLanguageTag("ja")));
        } else {
            authorName = String.format("%s(%s)%s", user.getDisplayName(), user.getLogin(),
                    messageSource.getMessage("channel.update.event-message", null, locale));
        }

        // Thumbnail
        final String thumbnailUrl = game.removeSizeMentionInBoxArtUrl();
        final DiscordEmbed.Thumbnail thumbnail = DiscordEmbed.Thumbnail.builder().url(thumbnailUrl).build();

        // Embed Area
        final String embedColor = Integer.toString(form.getDecimalColor());
        final String gameName = game.getName();
        final String embedDescription = gameName.equals("") ? messageSource.getMessage("game.none", null, locale) : messageSource.getMessage("game.prefix", null, locale) + gameName;
        final String embedTitle = event.getTitle();

        // Embed Field Area
        List<DiscordEmbed.Field> fields = new ArrayList<>();

        // Embed Footer Area
        final DiscordEmbed.Footer footer = new DiscordEmbed.Footer(messageSource.getMessage("channel.update.footer", null, locale), twitchLogoUrl);

        // Embed Timestamp Area
        final LocalDateTime generatedTime = LocalDateTime.now(ZoneOffset.UTC);

        final String languageIsoCode = LanguageIsoData.find(event.getLanguage()).getCode();
        final DiscordEmbed.Field languageField = new DiscordEmbed.Field(messageSource.getMessage("channel.update.language", null, locale),
                messageSource.getMessage("language." + languageIsoCode, null, locale), true);
        fields.add(languageField);

        final DiscordEmbed.Author author = new DiscordEmbed.Author(authorName, authorURL, authorProfileURL);

        final List<DiscordEmbed> discordEmbeds = new ArrayList<>();
        final DiscordEmbed discordEmbed = DiscordEmbed.builder()
                .author(author)
                .title(embedTitle)
                .url(authorURL)
                .description(embedDescription)
                .color(embedColor)
                .fields(fields)
                .footer(footer)
                .timestamp(String.valueOf(generatedTime))
                .thumbnail(thumbnail)
                .build();

        discordEmbeds.add(discordEmbed);

        return new DiscordEmbed.Webhook(form.getBotProfileId().getUsername(), form.getBotProfileId().getAvatarUrl(),
                form.getContent(), discordEmbeds);
    }
}
