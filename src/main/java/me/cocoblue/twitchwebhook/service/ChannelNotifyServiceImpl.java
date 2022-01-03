package me.cocoblue.twitchwebhook.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.data.LanguageIsoData;
import me.cocoblue.twitchwebhook.dto.discord.DiscordEmbed;
import me.cocoblue.twitchwebhook.dto.twitch.User;
import me.cocoblue.twitchwebhook.dto.twitch.eventsub.ChannelUpdateRequest;
import me.cocoblue.twitchwebhook.entity.SubscriptionFormEntity;
import me.cocoblue.twitchwebhook.service.twitch.UserInfoService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
@AllArgsConstructor
public class ChannelNotifyServiceImpl implements ChannelNotifyService {
    private final DiscordWebhookService discordWebhookService;
    private final NotificationFormService notificationFormService;
    private final UserInfoService userInfoService;

    private final String twitchUrl = "https://twitch.tv/";

    @Override
    public void sendChannelUpdateMessage(ChannelUpdateRequest.Body body) {
        log.info("Send Channel Update Message");

        long broadcasterId = Long.parseLong(body.getEvent().getBroadcasterUserId());
        final List<SubscriptionFormEntity> notifyForms = notificationFormService.getFormByBroadcasterIdAndType(broadcasterId, body.getSubscription().getType());
        log.debug("Received Notify Forms: " + notifyForms);

        User twitchUser;
        if(notifyForms.isEmpty()) {
            log.info("Notify Forms is empty. Do Nothing");
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
        final ChannelUpdateRequest.Event event = body.getEvent();

        // Author Area
        final String authorURL = twitchUrl + event.getBroadcasterUserLogin();
        final String authorProfileURL = user.getProfileImageUrl();
        final String authorName = event.getBroadcasterUserName() + "님의 채널 정보가 변경되었습니다.";

        // Embed Area
        final String embedColor = Integer.toString(form.getColor());
        final String gameName = event.getCategoryName();
        final String embedDescription = gameName.equals("") ? "지정된 게임 없음." : gameName;
        final String embedTitle = event.getTitle();

        // Embed Field Area
        List<DiscordEmbed.Field> fields = new ArrayList<>();

        // Embed Footer Area
        final DiscordEmbed.Footer footer = new DiscordEmbed.Footer("Twitch", null);

        final LocalDateTime generatedTime = LocalDateTime.now();
        final String generatedTimeString = generatedTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        final DiscordEmbed.Field changeTimeField = new DiscordEmbed.Field("변경 시간", generatedTimeString, true);
        fields.add(changeTimeField);

        final String languageIsoData = LanguageIsoData.find(event.getLanguage()).getKoreanName();
        final DiscordEmbed.Field languageField = new DiscordEmbed.Field("언어", languageIsoData, true);
        fields.add(languageField);

        final DiscordEmbed.Author author = new DiscordEmbed.Author(authorName, authorURL, authorProfileURL);

        List<DiscordEmbed> discordEmbeds = new ArrayList<>();
        final DiscordEmbed discordEmbed = new DiscordEmbed(author, embedTitle, authorURL, embedDescription, embedColor,
                fields, null, null, footer);
        discordEmbeds.add(discordEmbed);

        return new DiscordEmbed.Webhook(form.getUsername(), form.getAvatarUrl(), form.getContent(), discordEmbeds);
    }
}
