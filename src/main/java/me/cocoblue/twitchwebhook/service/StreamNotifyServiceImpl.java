package me.cocoblue.twitchwebhook.service;

import me.cocoblue.twitchwebhook.dto.Form;
import me.cocoblue.twitchwebhook.dto.GameIndex;
import me.cocoblue.twitchwebhook.dto.Log;
import me.cocoblue.twitchwebhook.dto.discord.DiscordWebhookMessage;
import me.cocoblue.twitchwebhook.dto.discord.embed.DiscordAuthor;
import me.cocoblue.twitchwebhook.dto.discord.embed.DiscordEmbed;
import me.cocoblue.twitchwebhook.dto.discord.embed.DiscordField;
import me.cocoblue.twitchwebhook.dto.discord.embed.DiscordFooter;
import me.cocoblue.twitchwebhook.vo.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class StreamNotifyServiceImpl {
    private final FormService formService;
    private final TwitchHelixApiServiceImpl twitchHelixApiService;
    private final LogService logService;
    private final GameIndexService gameIndexService;

    public StreamNotifyServiceImpl(FormService formService, TwitchHelixApiServiceImpl twitchHelixApiService,
                                   LogService logService, GameIndexService gameIndexService) {
        this.formService = formService;
        this.twitchHelixApiService = twitchHelixApiService;
        this.logService = logService;
        this.gameIndexService = gameIndexService;
    }

    // TODO: 메소드 간소화 필요.
    public DiscordWebhookMessage makeStartDiscordWebhookMessage(TwitchStreamNotification twitchStreamNotification,
                                                                Form form) {
        TwitchUser twitchUser = twitchHelixApiService.getUserInfoByLoginIdFromTwitch(twitchStreamNotification.getUserLogin());

        String authorName = twitchStreamNotification.getUserName() + "님이 방송을 시작했습니다.";
        String authorURL = "https://twitch.tv/" + twitchStreamNotification.getUserLogin();
        String authorProfileURL = twitchUser.getProfileImageUrl();
        DiscordAuthor discordAuthor = new DiscordAuthor(authorName, authorURL, authorProfileURL);

        String embedTitle = twitchStreamNotification.getTitle();
        String embedDescription = twitchStreamNotification.getGameName();
        if(twitchStreamNotification.getGameName().equals("")) {
            embedDescription = "지정된 게임 없음.";
        }
        String embedColor = Integer.toString(form.getColor());

        List<DiscordField> discordFields = new ArrayList<>();
        LocalDateTime koreanStartTime = twitchStreamNotification.getStartedAt().plusHours(9);
        String startTimeToString = koreanStartTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        DiscordField discordField = new DiscordField("시작 시간", startTimeToString, true);
        discordFields.add(discordField);

        DiscordFooter discordFooter = new DiscordFooter("Twitch", null);

        List<DiscordEmbed> discordEmbeds = new ArrayList<>();
        DiscordEmbed discordEmbed = new DiscordEmbed(discordAuthor, embedTitle, authorURL, embedDescription, embedColor,
                discordFields, null, null, discordFooter);
        discordEmbeds.add(discordEmbed);

        return new DiscordWebhookMessage(form.getUsername(), form.getAvatarUrl(), form.getContent(),
                discordEmbeds);
    }

    public void sendStartMessage(TwitchStreamNotification twitchStreamNotification) {
        int broadcasterId = Integer.parseInt(twitchStreamNotification.getUserId());
        List<Form> notifyForms = formService.getStartFormByBroadcasterIdAndType(broadcasterId, 0);

        for(Form notifyForm : notifyForms) {
            DiscordWebhookMessage discordWebhookMessage = makeStartDiscordWebhookMessage(twitchStreamNotification, notifyForm);
            sendDiscordWebHook(discordWebhookMessage, notifyForm.getWebhookUrl());
        }

    }

    public DiscordWebhookMessage makeEndDiscordWebhookMessage(String broadcasterId, Form form) {
        TwitchUser twitchUser = twitchHelixApiService.getUserInfoByBroadcasterIdFromTwitch(broadcasterId);

        String authorName = twitchUser.getDisplayName() + "님의 방송이 종료되었습니다.";
        String authorURL = "https://twitch.tv/" + twitchUser.getLogin();
        String authorProfileURL = twitchUser.getProfileImageUrl();
        DiscordAuthor discordAuthor = new DiscordAuthor(authorName, authorURL, authorProfileURL);

        String embedTitle = "";
        String embedDescription = "다음에 만나요!";
        String embedColor = Integer.toString(form.getColor());

        List<DiscordField> discordFields = new ArrayList<>();

        String endTimeToString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        DiscordField discordField = new DiscordField("종료 시간", endTimeToString, true);
        discordFields.add(discordField);

        DiscordFooter discordFooter = new DiscordFooter("Twitch", null);

        List<DiscordEmbed> discordEmbeds = new ArrayList<>();
        DiscordEmbed discordEmbed = new DiscordEmbed(discordAuthor, embedTitle, authorURL, embedDescription, embedColor,
                discordFields, null, null, discordFooter);
        discordEmbeds.add(discordEmbed);

        return new DiscordWebhookMessage(form.getUsername(), form.getAvatarUrl(), "", discordEmbeds);
    }

    public void sendEndMessage(String broadcasterId) {
        int broadcasterIdInt = Integer.parseInt(broadcasterId);
        List<Form> notifyForms = formService.getEndFormByBroadcasterIdAndType(broadcasterIdInt, 0);

        for(Form notifyForm : notifyForms) {
            DiscordWebhookMessage discordWebhookMessage = makeEndDiscordWebhookMessage(broadcasterId, notifyForm);
            sendDiscordWebHook(discordWebhookMessage, notifyForm.getWebhookUrl());
        }

    }

    public void sendDiscordWebHook(DiscordWebhookMessage discordWebhookMessage, String webhookUrl) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        HttpEntity<DiscordWebhookMessage> entity = new HttpEntity<>(discordWebhookMessage, headers);

        RestTemplate rt = new RestTemplate();
        rt.exchange(webhookUrl, HttpMethod.POST, entity, String.class);

    }

    public void insertLog(TwitchStreamNotification twitchStreamNotification) {
        GameIndex gameIndex = new GameIndex(Integer.parseInt(twitchStreamNotification.getGameId()),
                twitchStreamNotification.getGameName());
        gameIndexService.insertGameIndex(gameIndex);

        Log log = new Log();
        System.out.println(twitchStreamNotification.getId());
        log.setIdFromTwitch(twitchStreamNotification.getId());
        log.setStreamerId(Integer.parseInt(twitchStreamNotification.getUserId()));
        log.setTitle(twitchStreamNotification.getTitle());
        log.setStartedAt(twitchStreamNotification.getStartedAt().plusHours(9));
        log.setGameId(twitchStreamNotification.getGameIdInt());

        logService.insertLog(log);
    }
}
