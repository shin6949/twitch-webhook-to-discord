package me.cocoblue.twitchwebhook.service.twitch;

import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.data.LanguageIsoData;
import me.cocoblue.twitchwebhook.domain.discord.SubscriptionFormEntity;
import me.cocoblue.twitchwebhook.domain.twitch.PushSubscriptionFormEntity;
import me.cocoblue.twitchwebhook.dto.discord.DiscordEmbed;
import me.cocoblue.twitchwebhook.dto.twitch.Channel;
import me.cocoblue.twitchwebhook.dto.twitch.Game;
import me.cocoblue.twitchwebhook.dto.twitch.User;
import me.cocoblue.twitchwebhook.dto.twitch.eventsub.ChannelUpdateRequestEvent;
import me.cocoblue.twitchwebhook.dto.twitch.eventsub.NotificationEvent;
import me.cocoblue.twitchwebhook.service.FirebaseInitializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@Log4j2
public class ChannelNotifyService extends AbstractNotifyService {
    @Value("${twitch.logo-url}")
    private String twitchLogoUrl;
    private final MessageSource messageSource;

    public ChannelNotifyService(FirebaseInitializer firebaseInitializer, TwitchUserLogService twitchUserLogService,
                                EventSubService eventSubService, UserInfoService userInfoService,
                                GameInfoService gameInfoService, NotificationFormService notificationFormService,
                                MessageSource messageSource) {
        super(firebaseInitializer, twitchUserLogService, eventSubService, userInfoService, gameInfoService, notificationFormService);
        this.messageSource = messageSource;
    }

    @Override
    protected DiscordEmbed.Webhook makeDiscordEmbed(final NotificationEvent event, final SubscriptionFormEntity form,
                                                    final Channel channel, final User twitchUser, final Game game) {
        return makeChannelUpdateDiscordWebhook(event, form, twitchUser, game);
    }

    @Override
    protected Message makePushMessage(final User twitchUser, final Channel channel, final Game game,
                                      final PushSubscriptionFormEntity form, final NotificationEvent event) {

        final ChannelUpdateRequestEvent channelUpdateEvent = (ChannelUpdateRequestEvent) event;

        final Locale locale = Locale.forLanguageTag(form.getLanguageIsoData().getCode());
        final String messageTitle = messageSource.getMessage("api.notification.channel-update-event-title", new Object[]{twitchUser.getDisplayName()}, locale);

        final String streamerName = !twitchUser.getDisplayName().equals(twitchUser.getLogin()) ?
                String.format("%s(%s)", twitchUser.getDisplayName(), twitchUser.getLogin()) :
                twitchUser.getLogin();

        final String messageBody = String.format("%s\n%s\n%s",
                messageSource.getMessage("api.notification.streamer", new Object[]{streamerName}, locale),
                messageSource.getMessage("api.notification.title", new Object[]{channelUpdateEvent.getTitle()}, locale),
                messageSource.getMessage("api.notification.game", new Object[]{game.getName()}, locale));

        return Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(messageTitle)
                        .setBody(messageBody)
                        .build())
                .setToken(form.getRegistrationToken())
                .build();
    }

    @Override
    protected String getGameId(final NotificationEvent event, final Channel channel) {
        log.info("event.getClass(): " + event.getClass());
        final ChannelUpdateRequestEvent channelUpdateEvent = (ChannelUpdateRequestEvent) event;

        return channelUpdateEvent.getCategoryId();
    }

    private DiscordEmbed.Webhook makeChannelUpdateDiscordWebhook(final NotificationEvent event, final SubscriptionFormEntity form,
                                                                 final User user, final Game game) {
        // 설정한 언어 받아오기
        final Locale locale = Locale.forLanguageTag(form.getLanguageIsoData().getCode());
        final ChannelUpdateRequestEvent channelUpdateEvent = (ChannelUpdateRequestEvent) event;

        // Author Area
        final String twitchUrl = "https://twitch.tv/";
        final String authorURL = twitchUrl + channelUpdateEvent.getBroadcasterUserLogin();
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
        final String embedTitle = channelUpdateEvent.getTitle();

        // Embed Field Area
        List<DiscordEmbed.Field> fields = new ArrayList<>();

        // Embed Footer Area
        final DiscordEmbed.Footer footer = new DiscordEmbed.Footer(messageSource.getMessage("channel.update.footer", null, locale), twitchLogoUrl);

        // Embed Timestamp Area
        final LocalDateTime generatedTime = LocalDateTime.now(ZoneOffset.UTC);

        final String languageIsoCode = LanguageIsoData.find(channelUpdateEvent.getLanguage()).getCode();
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
