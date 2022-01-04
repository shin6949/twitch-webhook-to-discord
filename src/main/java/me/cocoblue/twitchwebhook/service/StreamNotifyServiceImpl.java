package me.cocoblue.twitchwebhook.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.data.LanguageIsoData;
import me.cocoblue.twitchwebhook.data.SubscriptionType;
import me.cocoblue.twitchwebhook.dto.discord.DiscordEmbed;
import me.cocoblue.twitchwebhook.dto.twitch.eventsub.StreamNotifyRequest;
import me.cocoblue.twitchwebhook.entity.SubscriptionFormEntity;
import me.cocoblue.twitchwebhook.service.twitch.UserInfoService;
import me.cocoblue.twitchwebhook.dto.twitch.Channel;
import me.cocoblue.twitchwebhook.dto.twitch.User;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Log4j2
@Service
@RequiredArgsConstructor
public class StreamNotifyServiceImpl implements StreamNotifyService {
    private final NotificationFormService notificationFormService;
    private final UserInfoService userInfoService;
    private final DiscordWebhookService discordWebhookService;
    private final MessageSource messageSource;

    private final String twitchUrl = "https://twitch.tv/";

    private DiscordEmbed.Webhook makeStreamOnlineDiscordWebhook(StreamNotifyRequest.Event event, SubscriptionFormEntity form, Channel channel, User user) {
        // Form의 Locale 얻기
        final Locale locale = Locale.forLanguageTag(form.getLanguageIsoData().getCode());

        // Author Area
        final String authorURL = twitchUrl + event.getBroadcasterUserLogin();
        final String authorProfileURL = user.getProfileImageUrl();
        final String authorName = String.format("%s%s", event.getBroadcasterUserName(),
                messageSource.getMessage("stream.online.event-message", null, locale));

        // Thumbnail
        final String thumbnailUrl = String.format("https://static-cdn.jtvnw.net/ttv-boxart/%s.jpg", channel.getGameId());
        final DiscordEmbed.Thumbnail thumbnail = DiscordEmbed.Thumbnail.builder().url(thumbnailUrl).build();

        // Embed Area
        final String embedColor = Integer.toString(form.getColor());
        final String gameName = channel.getGameName();
        final String embedDescription = gameName.equals("") ? messageSource.getMessage("game.none", null, locale) : gameName;
        final String embedTitle = channel.getTitle();

        // Embed Field Area
        List<DiscordEmbed.Field> fields = new ArrayList<>();

        // Embed Footer Area
        final DiscordEmbed.Footer footer = new DiscordEmbed.Footer(messageSource.getMessage("stream.online.footer", null, locale), null);

        // UTC, Embed Timestamp
        final LocalDateTime startTime = event.getStartedAt();

        final String languageIsoData = LanguageIsoData.find(channel.getBroadcasterLanguage()).getKoreanName();
        final DiscordEmbed.Field languageField = new DiscordEmbed.Field(messageSource.getMessage("stream.online.language", null, locale), languageIsoData, true);
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
                .timestamp(startTime.toString())
                .thumbnail(thumbnail)
                .build();

        discordEmbeds.add(discordEmbed);

        return new DiscordEmbed.Webhook(form.getUsername(), form.getAvatarUrl(), form.getContent(), discordEmbeds);
    }

    private DiscordEmbed.Webhook makeStreamOfflineDiscordWebhook(StreamNotifyRequest.Event event, SubscriptionFormEntity form, User user) {
        // Author Area
        final String authorURL = twitchUrl + event.getBroadcasterUserLogin();
        final String authorProfileURL = user.getProfileImageUrl();
        final String authorName = user.getDisplayName() + "님의 방송이 종료되었습니다.";

        // Embed Area
        final String embedColor = Integer.toString(form.getColor());
        final String embedDescription = "다음에 만나요!";
        final String embedTitle = "";

        // Embed Field Area
        List<DiscordEmbed.Field> fields = new ArrayList<>();

        // Embed Footer Area
        DiscordEmbed.Footer footer = new DiscordEmbed.Footer("Twitch", null);

        final String endTimeToString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        final DiscordEmbed.Field field = new DiscordEmbed.Field("종료 시간", endTimeToString, true);
        fields.add(field);

        final DiscordEmbed.Author author = new DiscordEmbed.Author(authorName, authorURL, authorProfileURL);

        List<DiscordEmbed> discordEmbeds = new ArrayList<>();
        final DiscordEmbed discordEmbed = new DiscordEmbed(author, embedTitle, authorURL, embedDescription, embedColor,
                fields, null, null, footer, null);
        discordEmbeds.add(discordEmbed);

        return new DiscordEmbed.Webhook(form.getUsername(), form.getAvatarUrl(), form.getContent(), discordEmbeds);
    }

    @Async
    @Override
    public void sendMessage(StreamNotifyRequest.Body body, Channel channel) {
        log.info("Stream Notify Service Called");
        log.debug("Received Body: " + body.toString());

        final long broadcasterId = Long.parseLong(body.getEvent().getBroadcasterUserId());
        final List<SubscriptionFormEntity> notifyForms = notificationFormService.getFormByBroadcasterIdAndType(broadcasterId, body.getSubscription().getType());
        log.debug("Got Forms From DB: " + notifyForms.toString());

        User twitchUser;
        if(notifyForms.isEmpty()) {
            log.info("Form is empty. Do nothing");
            return;
        } else {
            twitchUser = userInfoService.getUserInfoByBroadcasterIdFromTwitch(body.getEvent().getBroadcasterUserId());
            log.debug("Got User Info From Twitch: " + twitchUser.toString());
        }

        for (SubscriptionFormEntity notifyForm : notifyForms) {
            DiscordEmbed.Webhook discordWebhookMessage;
            if(body.getSubscription().getType().equals(SubscriptionType.STREAM_ONLINE.getTwitchName())) {
                discordWebhookMessage = makeStreamOnlineDiscordWebhook(body.getEvent(), notifyForm, channel, twitchUser);
            } else {
                discordWebhookMessage = makeStreamOfflineDiscordWebhook(body.getEvent(), notifyForm, twitchUser);
            }

            log.debug("Configured Webhook Message: " + discordWebhookMessage.toString());
            discordWebhookService.send(discordWebhookMessage, notifyForm.getWebhookUrl());
        }
    }
}
