package me.cocoblue.twitchwebhook.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.dto.Form;
import me.cocoblue.twitchwebhook.dto.GameIndex;
import me.cocoblue.twitchwebhook.dto.NotifyLog;
import me.cocoblue.twitchwebhook.dto.discord.DiscordEmbed;
import me.cocoblue.twitchwebhook.dto.discord.DiscordWebhookMessage;
import me.cocoblue.twitchwebhook.dto.discord.embed.Author;
import me.cocoblue.twitchwebhook.dto.discord.embed.Field;
import me.cocoblue.twitchwebhook.dto.discord.embed.Footer;
import me.cocoblue.twitchwebhook.service.twitch.UserInfoService;
import me.cocoblue.twitchwebhook.vo.twitch.Channel;
import me.cocoblue.twitchwebhook.vo.twitch.User;
import me.cocoblue.twitchwebhook.vo.twitch.eventsub.Event;
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

    // TODO: 메소드 간소화 필요.
    private DiscordWebhookMessage makeStartDiscordWebhookMessage(Event event, Form form, Channel channel) {
        final User twitchUser = userInfoService.getUserInfoByBroadcasterIdFromTwitch(event.getBroadcasterUserId());

        final String authorName = event.getBroadcasterUserName() + "님이 방송을 시작했습니다.";
        final String authorURL = "https://twitch.tv/" + event.getBroadcasterUserLogin();
        final String authorProfileURL = twitchUser.getProfileImageUrl();
        final Author author = new Author(authorName, authorURL, authorProfileURL);

        String embedTitle = channel.getTitle();
        String embedDescription = channel.getGameName();
        if (channel.getGameName().equals("")) {
            embedDescription = "지정된 게임 없음.";
        }
        String embedColor = Integer.toString(form.getColor());

        List<Field> fields = new ArrayList<>();
        LocalDateTime koreanStartTime = event.getStartedAt().plusHours(9);
        String startTimeToString = koreanStartTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        Field field = new Field("시작 시간", startTimeToString, true);
        fields.add(field);

        Footer footer = new Footer("Twitch", null);

        List<DiscordEmbed> discordEmbeds = new ArrayList<>();
        DiscordEmbed discordEmbed = new DiscordEmbed(author, embedTitle, authorURL, embedDescription, embedColor,
                fields, null, null, footer);
        discordEmbeds.add(discordEmbed);

        return new DiscordWebhookMessage(form.getUsername(), form.getAvatarUrl(), form.getContent(),
                discordEmbeds);
    }

    @Override
    @Async
    public void sendStartMessage(Event event, Channel channel) {
        int broadcasterId = Integer.parseInt(event.getBroadcasterUserId());
        List<Form> notifyForms = formService.getStartFormByBroadcasterIdAndType(broadcasterId, 0);

        for (Form notifyForm : notifyForms) {
            DiscordWebhookMessage discordWebhookMessage = makeStartDiscordWebhookMessage(event, notifyForm, channel);
            sendDiscordWebHook(discordWebhookMessage, notifyForm.getWebhookUrl());
        }

    }

    @Override
    public DiscordWebhookMessage makeEndDiscordWebhookMessage(String broadcasterId, Form form) {
        User user = userInfoService.getUserInfoByBroadcasterIdFromTwitch(broadcasterId);

        String authorName = user.getDisplayName() + "님의 방송이 종료되었습니다.";
        String authorURL = "https://twitch.tv/" + user.getLogin();
        String authorProfileURL = user.getProfileImageUrl();
        Author author = new Author(authorName, authorURL, authorProfileURL);

        String embedTitle = "";
        String embedDescription = "다음에 만나요!";
        String embedColor = Integer.toString(form.getColor());

        List<Field> fields = new ArrayList<>();

        String endTimeToString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        Field field = new Field("종료 시간", endTimeToString, true);
        fields.add(field);

        Footer footer = new Footer("Twitch", null);

        List<DiscordEmbed> discordEmbeds = new ArrayList<>();
        DiscordEmbed discordEmbed = new DiscordEmbed(author, embedTitle, authorURL, embedDescription, embedColor,
                fields, null, null, footer);
        discordEmbeds.add(discordEmbed);

        return new DiscordWebhookMessage(form.getUsername(), form.getAvatarUrl(), "", discordEmbeds);
    }

    @Override
    public void sendEndMessage(String broadcasterId) {
        int broadcasterIdInt = Integer.parseInt(broadcasterId);
        List<Form> notifyForms = formService.getEndFormByBroadcasterIdAndType(broadcasterIdInt, 0);

        for (Form notifyForm : notifyForms) {
            DiscordWebhookMessage discordWebhookMessage = makeEndDiscordWebhookMessage(broadcasterId, notifyForm);
            sendDiscordWebHook(discordWebhookMessage, notifyForm.getWebhookUrl());
        }

    }

    @Override
    public void sendDiscordWebHook(DiscordWebhookMessage discordWebhookMessage, String webhookUrl) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        HttpEntity<DiscordWebhookMessage> entity = new HttpEntity<>(discordWebhookMessage, headers);

        RestTemplate rt = new RestTemplate();
        rt.exchange(webhookUrl, HttpMethod.POST, entity, String.class);

    }

    @Override
    @Async
    public void insertLog(Event event, Channel channel) {
        GameIndex gameIndex = channel.toGameIndex();
        gameIndexService.insertGameIndex(gameIndex);

        NotifyLog notifyLog = new NotifyLog();
        notifyLog.setIdFromTwitch(event.getId());
        notifyLog.setStreamerId(Integer.parseInt(event.getBroadcasterUserId()));
        notifyLog.setTitle(channel.getTitle());
        notifyLog.setStartedAt(event.getStartedAt().plusHours(9));
        notifyLog.setGameId(gameIndex.getId());

        notifyLogService.insertLog(notifyLog);
    }
}
