package me.cocoblue.twitchwebhook.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.data.LanguageIsoData;
import me.cocoblue.twitchwebhook.dto.discord.DiscordEmbed;
import me.cocoblue.twitchwebhook.dto.twitch.User;
import me.cocoblue.twitchwebhook.dto.twitch.eventsub.ChannelUpdateRequest;
import me.cocoblue.twitchwebhook.entity.SubscriptionFormEntity;
import me.cocoblue.twitchwebhook.service.twitch.EventSubService;
import me.cocoblue.twitchwebhook.service.twitch.UserInfoService;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@Log4j2
@AllArgsConstructor
public class ChannelNotifyServiceImpl implements ChannelNotifyService {
    private final DiscordWebhookService discordWebhookService;
    private final OauthTokenService oauthTokenService;
    private final EventSubService eventSubService;
    private final NotificationFormService notificationFormService;
    private final UserInfoService userInfoService;
    private final MessageSource messageSource;

    private final String twitchUrl = "https://twitch.tv/";

    @Override
    public void sendChannelUpdateMessage(ChannelUpdateRequest.Body body) {
        log.info("Send Channel Update Message");
        log.debug("Received Body: " + body);

        long broadcasterId = Long.parseLong(body.getEvent().getBroadcasterUserId());
        final List<SubscriptionFormEntity> notifyForms = notificationFormService.getFormByBroadcasterIdAndType(broadcasterId, body.getSubscription().getType());
        log.debug("Received Notify Forms: " + notifyForms);

        User twitchUser;
        if(notifyForms.isEmpty()) {
            log.info("Form is empty. Delete the Subscription");
            final String accessToken = oauthTokenService.getAppTokenFromTwitch().getAccessToken();
            eventSubService.deleteEventSub(body.getSubscription().getId(), accessToken);
            oauthTokenService.revokeAppTokenToTwitch(accessToken);
            return;
        } else {
            twitchUser = userInfoService.getUserInfoByBroadcasterIdFromTwitch(body.getEvent().getBroadcasterUserId());
        }

        for (SubscriptionFormEntity notifyForm : notifyForms) {
            final DiscordEmbed.Webhook discordWebhookMessage = makeChannelUpdateDiscordWebhook(body, notifyForm, twitchUser);
            log.debug("Made Webhook Message: " + discordWebhookMessage);

            discordWebhookService.send(discordWebhookMessage, notifyForm.getWebhookUrl());
        }
    }

    private DiscordEmbed.Webhook makeChannelUpdateDiscordWebhook(ChannelUpdateRequest.Body body, SubscriptionFormEntity form, User user) {
        // 설정한 언어 받아오기
        final Locale locale = Locale.forLanguageTag(form.getLanguageIsoData().getCode());
        log.debug("locale: " + locale);

        final ChannelUpdateRequest.Event event = body.getEvent();

        // Author Area
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
        final String thumbnailUrl = String.format("https://static-cdn.jtvnw.net/ttv-boxart/%s.jpg", event.getCategoryId());
        final DiscordEmbed.Thumbnail thumbnail = DiscordEmbed.Thumbnail.builder().url(thumbnailUrl).build();

        // Embed Area
        final String embedColor = Integer.toString(form.getColor());
        final String gameName = event.getCategoryName();
        final String embedDescription = gameName.equals("") ? messageSource.getMessage("game.none", null, locale) : messageSource.getMessage("game.prefix", null, locale) + gameName;
        final String embedTitle = event.getTitle();

        // Embed Field Area
        List<DiscordEmbed.Field> fields = new ArrayList<>();

        // Embed Footer Area
        final DiscordEmbed.Footer footer = new DiscordEmbed.Footer(messageSource.getMessage("channel.update.footer", null, locale), null);

        // Embed Timestamp Area
        final LocalDateTime generatedTime = LocalDateTime.now(ZoneOffset.UTC);

        final String languageIsoCode = LanguageIsoData.find(event.getLanguage()).getCode();
        final DiscordEmbed.Field languageField = new DiscordEmbed.Field(messageSource.getMessage("channel.update.language", null, locale),
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
                .timestamp(generatedTime.toString())
                .thumbnail(thumbnail)
                .build();

        discordEmbeds.add(discordEmbed);

        return new DiscordEmbed.Webhook(form.getUsername(), form.getAvatarUrl(), form.getContent(), discordEmbeds);
    }
}
