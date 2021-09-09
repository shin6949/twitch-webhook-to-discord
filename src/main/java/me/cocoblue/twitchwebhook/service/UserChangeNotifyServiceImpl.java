package me.cocoblue.twitchwebhook.service;

import lombok.AllArgsConstructor;
import me.cocoblue.twitchwebhook.dto.Form;
import me.cocoblue.twitchwebhook.dto.discord.DiscordEmbed;
import me.cocoblue.twitchwebhook.dto.discord.DiscordWebhookMessage;
import me.cocoblue.twitchwebhook.dto.discord.embed.Author;
import me.cocoblue.twitchwebhook.dto.discord.embed.Field;
import me.cocoblue.twitchwebhook.dto.discord.embed.Footer;
import me.cocoblue.twitchwebhook.vo.twitch.notification.UserChange;
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
@AllArgsConstructor
public class UserChangeNotifyServiceImpl {
    private final FormService formService;

    public DiscordWebhookMessage makeDiscordWebhookMessage(UserChange userChange,
                                                           Form form) {
        String authorName = userChange.getDisplayName() + "님의 정보가 변경되었습니다.";
        String authorURL = "https://twitch.tv/" + userChange.getDisplayName();
        String authorProfileURL = userChange.getProfileImageUrl();
        Author author = new Author(authorName, authorURL, authorProfileURL);

        String embedTitle = userChange.getDisplayName() + "님의 정보가 변경되었습니다.";
        String embedDescription = "Test";
        String embedColor = "418422";

        List<Field> fields = new ArrayList<>();
        Field field = new Field("name", "value", true);
        fields.add(field);

        Footer footer = new Footer("Twitch", null);

        List<DiscordEmbed> discordEmbeds = new ArrayList<>();
        DiscordEmbed discordEmbed = new DiscordEmbed(author, embedTitle, authorURL, embedDescription, embedColor,
                fields, null, null, footer);
        discordEmbeds.add(discordEmbed);

        return new DiscordWebhookMessage(form.getUsername(), form.getAvatarUrl(), form.getContent(),
                discordEmbeds);
    }

    public void sendDiscordWebHook(UserChange userChange) {
        int broadcasterId = Integer.parseInt(userChange.getId());
        List<Form> notifyForms = formService.getStartFormByBroadcasterIdAndType(broadcasterId, 1);

        for (Form notifyForm : notifyForms) {
            DiscordWebhookMessage discordWebhookMessage = makeDiscordWebhookMessage(userChange, notifyForm);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/json");
            HttpEntity<DiscordWebhookMessage> entity = new HttpEntity<>(discordWebhookMessage, headers);

            RestTemplate rt = new RestTemplate();

            ResponseEntity<String> response = rt.exchange(
                    notifyForm.getWebhookUrl(), //{요청할 서버 주소}
                    HttpMethod.POST, //{요청할 방식}
                    entity, // {요청할 때 보낼 데이터}
                    String.class);
        }

    }
}
