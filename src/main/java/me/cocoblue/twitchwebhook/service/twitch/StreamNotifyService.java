package me.cocoblue.twitchwebhook.service.twitch;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.data.LanguageIsoData;
import me.cocoblue.twitchwebhook.data.TwitchSubscriptionType;
import me.cocoblue.twitchwebhook.domain.NotificationLogEntity;
import me.cocoblue.twitchwebhook.domain.SubscriptionFormEntity;
import me.cocoblue.twitchwebhook.dto.discord.DiscordEmbed;
import me.cocoblue.twitchwebhook.dto.twitch.Channel;
import me.cocoblue.twitchwebhook.dto.twitch.Game;
import me.cocoblue.twitchwebhook.dto.twitch.User;
import me.cocoblue.twitchwebhook.dto.twitch.eventsub.StreamNotifyRequest;
import me.cocoblue.twitchwebhook.service.DiscordWebhookService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class StreamNotifyService {
    @Value("${twitch.logo-url}")
    private String twitchLogoUrl;

    private final NotificationFormService notificationFormService;
    private final EventSubService eventSubService;
    private final OauthTokenService oauthTokenService;
    private final UserInfoService userInfoService;
    private final DiscordWebhookService discordWebhookService;
    private final MessageSource messageSource;
    private final GameInfoService gameInfoService;
    private final TwitchUserLogService twitchUserLogService;

    private final String twitchUrl = "https://twitch.tv/";

    private DiscordEmbed.Webhook makeStreamOnlineDiscordWebhook(StreamNotifyRequest.Event event, SubscriptionFormEntity form,
                                                                Channel channel, User user) {
        // Form의 Locale 얻기
        final Locale locale = Locale.forLanguageTag(form.getLanguageIsoData().getCode());

        // Game 정보 얻어오기
        final Game game = gameInfoService.getGameInfoByIdFromTwitch(channel.getGameId());

        // Author Area
        final String authorURL = twitchUrl + event.getBroadcasterUserLogin();
        final String authorProfileURL = user.getProfileImageUrl();
        String authorName;
        if(user.getDisplayName().equals(user.getLogin())) {
            authorName = String.format("%s%s", user.getDisplayName(),
                    messageSource.getMessage("stream.online.event-message", null, locale));
        } else {
            authorName = String.format("%s(%s)%s", user.getDisplayName(), user.getLogin(),
                    messageSource.getMessage("stream.online.event-message", null, locale));
        }

        // Thumbnail
        final String thumbnailUrl = game.removeSizeMentionInBoxArtUrl();
        final DiscordEmbed.Thumbnail thumbnail = DiscordEmbed.Thumbnail.builder().url(thumbnailUrl).build();

        // Embed Area
        final String embedColor = Integer.toString(form.getDecimalColor());

        final String gameName = game.getName();
        final String embedDescription = gameName.equals("") ? messageSource.getMessage("game.none", null, locale) : messageSource.getMessage("game.prefix", null, locale) + gameName;
        final String embedTitle = channel.getTitle();

        // Embed Field Area
        List<DiscordEmbed.Field> fields = new ArrayList<>();

        // Embed Footer Area
        final DiscordEmbed.Footer footer = new DiscordEmbed.Footer(messageSource.getMessage("stream.online.footer", null, locale), twitchLogoUrl);

        // UTC, Embed Timestamp
        final LocalDateTime startTime = event.getStartedAt();

        final String languageIsoCode = LanguageIsoData.find(channel.getBroadcasterLanguage()).getCode();
        final DiscordEmbed.Field languageField = new DiscordEmbed.Field(messageSource.getMessage("stream.online.language", null, locale),
                messageSource.getMessage("language." + languageIsoCode, null, locale), true);
        fields.add(languageField);

        final DiscordEmbed.Author author = new DiscordEmbed.Author(authorName, authorURL, authorProfileURL);

        List<DiscordEmbed> discordEmbeds = new ArrayList<>();
        final DiscordEmbed discordEmbed = DiscordEmbed.builder()
                .author(author)
                .title(embedTitle)
                .url(authorURL)
                .description(embedDescription)
                .color(embedColor)
                .fields(fields)
                .footer(footer)
                .timestamp(String.valueOf(startTime))
                .thumbnail(thumbnail)
                .build();

        discordEmbeds.add(discordEmbed);

        return new DiscordEmbed.Webhook(form.getBotProfileId().getUsername(),
                form.getBotProfileId().getAvatarUrl(), form.getContent(), discordEmbeds);
    }

    private DiscordEmbed.Webhook makeStreamOfflineDiscordWebhook(StreamNotifyRequest.Event event, SubscriptionFormEntity form, User user) {
        // Form의 Locale 얻기
        final Locale locale = Locale.forLanguageTag(form.getLanguageIsoData().getCode());
        log.debug("locale: " + locale);

        // Author Area
        final String authorURL = twitchUrl + event.getBroadcasterUserLogin();
        final String authorProfileURL = user.getProfileImageUrl();
        String authorName;
        if(user.getDisplayName().equals(user.getLogin())) {
            authorName = String.format("%s%s", user.getDisplayName(),
                    messageSource.getMessage("stream.offline.event-message", null, locale));
        } else {
            authorName = String.format("%s(%s)%s", user.getDisplayName(), user.getLogin(),
                    messageSource.getMessage("stream.offline.event-message", null, locale));
        }

        // Embed Area
        final String embedColor = Integer.toString(form.getDecimalColor());
        final String embedDescription = messageSource.getMessage("stream.offline.embed-description", null, locale);
        final String embedTitle = messageSource.getMessage("stream.offline.embed-title", null, locale);
        final DiscordEmbed.Image image = DiscordEmbed.Image.builder()
                .url(user.getOfflineImageUrl())
                .height(300)
                .width(300)
                .build();

        // Embed Field Area
        List<DiscordEmbed.Field> fields = new ArrayList<>();

        // Embed Footer Area
        DiscordEmbed.Footer footer = new DiscordEmbed.Footer(messageSource.getMessage("stream.offline.footer", null, locale), twitchLogoUrl);

        final LocalDateTime endTime = LocalDateTime.now(ZoneOffset.UTC);

        final DiscordEmbed.Author author = new DiscordEmbed.Author(authorName, authorURL, authorProfileURL);

        List<DiscordEmbed> discordEmbeds = new ArrayList<>();

        final DiscordEmbed discordEmbed = DiscordEmbed.builder()
                .author(author)
                .title(embedTitle)
                .url(authorURL)
                .image(image)
                .description(embedDescription)
                .color(embedColor)
                .fields(fields)
                .footer(footer)
                .timestamp(String.valueOf(endTime))
                .build();
        discordEmbeds.add(discordEmbed);

        return new DiscordEmbed.Webhook(form.getBotProfileId().getUsername(),
                form.getBotProfileId().getAvatarUrl(), form.getContent(), discordEmbeds);
    }

    @Async
    public void sendMessage(StreamNotifyRequest.Body body, Channel channel, NotificationLogEntity notificationLogEntity) {
        log.info("Stream Notify Service Called");
        log.debug("Received Body: " + body.toString());

        final long broadcasterId = Long.parseLong(body.getEvent().getBroadcasterUserId());
        final List<SubscriptionFormEntity> notifyForms = notificationFormService.getFormByBroadcasterIdAndType(broadcasterId, body.getSubscription().getType());
        log.debug("Got Forms From DB: " + notifyForms.toString());

        if(notifyForms.isEmpty()) {
            log.info("Form is empty. Delete the Subscription");
            final String accessToken = oauthTokenService.getAppTokenFromTwitch().getAccessToken();
            eventSubService.deleteEventSub(body.getSubscription().getId());
            oauthTokenService.revokeAppTokenToTwitch(accessToken);
            return;
        }

        final List<SubscriptionFormEntity> filteredNotifyForms = notifyForms
                .stream()
                .filter(notifyForm -> twitchUserLogService.isNotInInterval(body.getEvent().getBroadcasterUserId(), notifyForm.getTwitchSubscriptionType(), notifyForm.getIntervalMinute()))
                .collect(Collectors.toList());

        if(filteredNotifyForms.isEmpty()) {
            log.info("Filtered NotifyForms Is Empty. Finish The Processing");
            return;
        }

        final User twitchUser = userInfoService.getUserInfoByBroadcasterIdFromTwitch(body.getEvent().getBroadcasterUserId());
        log.debug("Got User Info From Twitch: " + twitchUser.toString());

        filteredNotifyForms.parallelStream().forEach(notifyForm -> {
            DiscordEmbed.Webhook discordWebhookMessage;
            if(notifyForm.getTwitchSubscriptionType() == TwitchSubscriptionType.STREAM_ONLINE) {
                discordWebhookMessage = makeStreamOnlineDiscordWebhook(body.getEvent(), notifyForm, channel, twitchUser);
            } else {
                discordWebhookMessage = makeStreamOfflineDiscordWebhook(body.getEvent(), notifyForm, twitchUser);
            }

            log.debug("Configured Webhook Message: " + discordWebhookMessage);
            final HttpStatus httpStatus = discordWebhookService.send(discordWebhookMessage, notifyForm.getWebhookId().getWebhookUrl());

            if(notificationLogEntity != null) {
                twitchUserLogService.insertUserLog(notifyForm, notificationLogEntity, httpStatus.is2xxSuccessful());
            }
        });
    }
}
