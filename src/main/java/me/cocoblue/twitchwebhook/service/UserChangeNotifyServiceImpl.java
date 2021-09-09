package me.cocoblue.twitchwebhook.service;

import me.cocoblue.twitchwebhook.dto.Form;
import me.cocoblue.twitchwebhook.dto.discord.DiscordWebhookMessage;
import me.cocoblue.twitchwebhook.dto.discord.embed.DiscordAuthor;
import me.cocoblue.twitchwebhook.dto.discord.embed.DiscordEmbed;
import me.cocoblue.twitchwebhook.dto.discord.embed.DiscordField;
import me.cocoblue.twitchwebhook.dto.discord.embed.DiscordFooter;
import me.cocoblue.twitchwebhook.vo.TwitchUserChangeNotification;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserChangeNotifyServiceImpl {
    private final FormService formService;

    public UserChangeNotifyServiceImpl(FormService formService) {
        this.formService = formService;
    }

    public DiscordWebhookMessage makeDiscordWebhookMessage(TwitchUserChangeNotification twitchUserChangeNotification,
                                                           Form form) {
        String authorName = twitchUserChangeNotification.getDisplayName() + "님의 정보가 변경되었습니다.";
        String authorURL = "https://twitch.tv/" + twitchUserChangeNotification.getDisplayName();
        String authorProfileURL = twitchUserChangeNotification.getProfileImageUrl();
        DiscordAuthor discordAuthor = new DiscordAuthor(authorName, authorURL, authorProfileURL);

        String embedTitle = twitchUserChangeNotification.getDisplayName() + "님의 정보가 변경되었습니다.";
        String embedDescription = "Test";
        String embedColor = "418422";

        List<DiscordField> discordFields = new ArrayList<>();
        DiscordField discordField = new DiscordField("name", "value", true);
        discordFields.add(discordField);

        DiscordFooter discordFooter = new DiscordFooter("Twitch", null);

        List<DiscordEmbed> discordEmbeds = new ArrayList<>();
        DiscordEmbed discordEmbed = new DiscordEmbed(discordAuthor, embedTitle, authorURL, embedDescription, embedColor,
                discordFields, null, null, discordFooter);
        discordEmbeds.add(discordEmbed);

        return new DiscordWebhookMessage(form.getUsername(), form.getAvatarUrl(), form.getContent(),
                discordEmbeds);
    }

    public boolean sendDiscordWebHook(TwitchUserChangeNotification twitchUserChangeNotification) {
        int broadcasterId = Integer.parseInt(twitchUserChangeNotification.getId());
        List<Form> notifyForms = formService.getStartFormByBroadcasterIdAndType(broadcasterId, 1);

        for(Form notifyForm : notifyForms) {
            DiscordWebhookMessage discordWebhookMessage = makeDiscordWebhookMessage(twitchUserChangeNotification, notifyForm);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/json");
            HttpEntity<DiscordWebhookMessage> entity = new HttpEntity<DiscordWebhookMessage>(discordWebhookMessage, headers);

            RestTemplate rt = new RestTemplate();

            ResponseEntity<String> response = rt.exchange(
                    notifyForm.getWebhookUrl(), //{요청할 서버 주소}
                    HttpMethod.POST, //{요청할 방식}
                    entity, // {요청할 때 보낼 데이터}
                    String.class);
        }

        return true;
    }
}
