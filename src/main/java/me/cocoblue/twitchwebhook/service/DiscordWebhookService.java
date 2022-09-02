package me.cocoblue.twitchwebhook.service;

import me.cocoblue.twitchwebhook.dto.discord.DiscordEmbed;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DiscordWebhookService {
    @Async
    public HttpStatus send(DiscordEmbed.Webhook discordWebhookMessage, String webhookUrl) {
        final HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        final HttpEntity<DiscordEmbed.Webhook> entity = new HttpEntity<>(discordWebhookMessage, headers);

        final RestTemplate rt = new RestTemplate();
        return rt.exchange(webhookUrl, HttpMethod.POST, entity, String.class).getStatusCode();
    }
}
