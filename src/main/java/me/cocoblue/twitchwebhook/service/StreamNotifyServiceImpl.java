package me.cocoblue.twitchwebhook.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.dto.discord.DiscordEmbed;
import me.cocoblue.twitchwebhook.dto.twitch.eventsub.StreamNotifyRequest;
import me.cocoblue.twitchwebhook.entity.BroadcasterIdEntity;
import me.cocoblue.twitchwebhook.entity.GameIndexEntity;
import me.cocoblue.twitchwebhook.entity.SubscriptionFormEntity;
import me.cocoblue.twitchwebhook.entity.NotificationLogEntity;
import me.cocoblue.twitchwebhook.service.twitch.UserInfoService;
import me.cocoblue.twitchwebhook.dto.twitch.Channel;
import me.cocoblue.twitchwebhook.dto.twitch.User;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class StreamNotifyServiceImpl implements StreamNotifyService {
    private final FormService formService;
    private final UserInfoService userInfoService;
    private final NotifyLogService notifyLogService;
    private final GameIndexService gameIndexService;

    private DiscordEmbed.Webhook makeStreamDiscordWebhook(StreamNotifyRequest.Event event, SubscriptionFormEntity form, Channel channel, User user, boolean isStart) {
        // Author Area
        final String authorURL = "https://twitch.tv/" + event.getBroadcasterUserLogin();
        final String authorProfileURL = user.getProfileImageUrl();
        String authorName;

        // Embed Area
        final String embedColor = Integer.toString(form.getColor());
        String embedDescription;
        String embedTitle;

        // Embed Field Area
        List<DiscordEmbed.Field> fields = new ArrayList<>();

        // Embed Footer Area
        DiscordEmbed.Footer footer = new DiscordEmbed.Footer("Twitch", null);

        // 알림 유형에 따라 분기
        if(isStart) {
            authorName = event.getBroadcasterUserName() + "님이 방송을 시작했습니다.";

            embedTitle = channel.getTitle();
            embedDescription = channel.getGameName();
            if (channel.getGameName().equals("")) {
                embedDescription = "지정된 게임 없음.";
            }

            LocalDateTime koreanStartTime = event.getStartedAt().plusHours(9);
            String startTimeToString = koreanStartTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            DiscordEmbed.Field field = new DiscordEmbed.Field("시작 시간", startTimeToString, true);
            fields.add(field);

        } else {
            authorName = user.getDisplayName() + "님의 방송이 종료되었습니다.";

            embedTitle = "";
            embedDescription = "다음에 만나요!";

            String endTimeToString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            DiscordEmbed.Field field = new DiscordEmbed.Field("종료 시간", endTimeToString, true);
            fields.add(field);
        }

        final DiscordEmbed.Author author = new DiscordEmbed.Author(authorName, authorURL, authorProfileURL);
        List<DiscordEmbed> discordEmbeds = new ArrayList<>();
        DiscordEmbed discordEmbed = new DiscordEmbed(author, embedTitle, authorURL, embedDescription, embedColor,
                fields, null, null, footer);
        discordEmbeds.add(discordEmbed);

        return new DiscordEmbed.Webhook(form.getUsername(), form.getAvatarUrl(), form.getContent(),
                discordEmbeds);
    }

    @Async
    public void sendMessage(StreamNotifyRequest.Body body, Channel channel) {
        long broadcasterId = Long.parseLong(body.getEvent().getBroadcasterUserId());
        List<SubscriptionFormEntity> notifyForms = formService.getFormByBroadcasterIdAndType(broadcasterId, body.getSubscription().getType());

        User twitchUser = null;
        if(!notifyForms.isEmpty()) {
            twitchUser = userInfoService.getUserInfoByBroadcasterIdFromTwitch(body.getEvent().getBroadcasterUserId());
        }

        for (SubscriptionFormEntity notifyForm : notifyForms) {
            boolean isOnline = body.getSubscription().getType().equals("stream.online");
            DiscordEmbed.Webhook discordWebhookMessage = makeStreamDiscordWebhook(body.getEvent(), notifyForm, channel, twitchUser, isOnline);

            sendDiscordWebHook(discordWebhookMessage, notifyForm.getWebhookUrl());
        }

    }

    @Async
    void sendDiscordWebHook(DiscordEmbed.Webhook discordWebhookMessage, String webhookUrl) {
        final HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        final HttpEntity<DiscordEmbed.Webhook> entity = new HttpEntity<>(discordWebhookMessage, headers);

        final RestTemplate rt = new RestTemplate();
        rt.exchange(webhookUrl, HttpMethod.POST, entity, String.class);
    }

    @Override
    @Async
    public void insertLog(StreamNotifyRequest.Event event, Channel channel) {
        final GameIndexEntity gameIndexEntity = channel.toGameIndexEntity();
        gameIndexService.insertGameIndex(gameIndexEntity);

        final BroadcasterIdEntity broadcasterIdEntity = BroadcasterIdEntity.builder()
                .id(Long.parseLong(event.getBroadcasterUserId()))
                .build();

        final NotificationLogEntity notificationLogEntity = NotificationLogEntity.builder()
                .idFromTwitch(event.getId())
                .broadcasterIdEntity(broadcasterIdEntity)
                .title(channel.getTitle())
                .startedAt(event.getStartedAt().plusHours(9))
                .gameIndexEntity(gameIndexEntity)
                .build();

        notifyLogService.insertLog(notificationLogEntity);
    }
}
