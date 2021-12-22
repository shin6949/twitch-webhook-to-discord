package me.cocoblue.twitchwebhook.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.dto.discord.DiscordEmbed;
import me.cocoblue.twitchwebhook.dto.twitch.eventsub.StreamNotifyRequest;
import me.cocoblue.twitchwebhook.entity.SubscriptionFormEntity;
import me.cocoblue.twitchwebhook.service.twitch.UserInfoService;
import me.cocoblue.twitchwebhook.dto.twitch.Channel;
import me.cocoblue.twitchwebhook.dto.twitch.User;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class StreamNotifyServiceImpl implements StreamNotifyService {
    private final NotificationFormService notificationFormService;
    private final UserInfoService userInfoService;
    private final DiscordWebhookService discordWebhookService;

    private final String twitchUrl = "https://twitch.tv/";

    private DiscordEmbed.Webhook makeStreamOnlineDiscordWebhook(StreamNotifyRequest.Event event, SubscriptionFormEntity form, Channel channel, User user) {
        // Author Area
        final String authorURL = twitchUrl + event.getBroadcasterUserLogin();
        final String authorProfileURL = user.getProfileImageUrl();
        final String authorName = event.getBroadcasterUserName() + "님이 방송을 시작했습니다.";

        // Embed Area
        final String embedColor = Integer.toString(form.getColor());
        final String gameName = channel.getGameName();
        final String embedDescription = gameName.equals("") ? gameName : "지정된 게임 없음.";
        final String embedTitle = channel.getTitle();

        // Embed Field Area
        List<DiscordEmbed.Field> fields = new ArrayList<>();

        // Embed Footer Area
        final DiscordEmbed.Footer footer = new DiscordEmbed.Footer("Twitch", null);

        final LocalDateTime koreanStartTime = event.getStartedAt().plusHours(9);
        final String startTimeToString = koreanStartTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        final DiscordEmbed.Field field = new DiscordEmbed.Field("시작 시간", startTimeToString, true);
        fields.add(field);

        final DiscordEmbed.Author author = new DiscordEmbed.Author(authorName, authorURL, authorProfileURL);

        List<DiscordEmbed> discordEmbeds = new ArrayList<>();
        final DiscordEmbed discordEmbed = new DiscordEmbed(author, embedTitle, authorURL, embedDescription, embedColor,
                fields, null, null, footer);
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
                fields, null, null, footer);
        discordEmbeds.add(discordEmbed);

        return new DiscordEmbed.Webhook(form.getUsername(), form.getAvatarUrl(), form.getContent(), discordEmbeds);
    }

    @Async
    @Override
    public void sendMessage(StreamNotifyRequest.Body body, Channel channel) {
        long broadcasterId = Long.parseLong(body.getEvent().getBroadcasterUserId());
        final List<SubscriptionFormEntity> notifyForms = notificationFormService.getFormByBroadcasterIdAndType(broadcasterId, body.getSubscription().getType());

        User twitchUser = null;
        if(notifyForms.isEmpty()) {
            return;
        } else {
            twitchUser = userInfoService.getUserInfoByBroadcasterIdFromTwitch(body.getEvent().getBroadcasterUserId());
        }

        for (SubscriptionFormEntity notifyForm : notifyForms) {
            DiscordEmbed.Webhook discordWebhookMessage;
            if(body.getSubscription().getType().equals("stream.online")) {
                discordWebhookMessage = makeStreamOnlineDiscordWebhook(body.getEvent(), notifyForm, channel, twitchUser);
            } else {
                discordWebhookMessage = makeStreamOfflineDiscordWebhook(body.getEvent(), notifyForm, twitchUser);
            }

            discordWebhookService.send(discordWebhookMessage, notifyForm.getWebhookUrl());
        }
    }
}
