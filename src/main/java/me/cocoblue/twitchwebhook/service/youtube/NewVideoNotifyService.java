package me.cocoblue.twitchwebhook.service.youtube;

import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.Video;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.data.YouTubeSubscriptionType;
import me.cocoblue.twitchwebhook.domain.youtube.*;
import me.cocoblue.twitchwebhook.dto.discord.DiscordEmbed;
import me.cocoblue.twitchwebhook.service.DiscordWebhookService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class NewVideoNotifyService {
    @Value("${youtube.logo-url}")
    private String youTubeLogoUrl;

    private final YouTubeSubscriptionFormRepository youTubeSubscriptionFormRepository;
    private final YouTubeChannelInfoRepository youTubeChannelInfoRepository;
    private final MessageSource messageSource;
    private final DiscordWebhookService discordWebhookService;
    private final YoutubeNotificationLogService youtubeNotificationLogService;
    private final YouTubeUserLogService youTubeUserLogService;

    public void sendLiveStreamMessage(Video video, Channel channel) {
        log.info("Send Live Stream Message");

        final List<YouTubeSubscriptionFormEntity> notifyForms = getValidNotificationForms(channel.getId(), YouTubeSubscriptionType.LIVE_START);
        if(notifyForms == null) {
            log.info("Received Null. Finish The Processing");
            return;
        }

        final YouTubeNotificationLogEntity youTubeNotificationLogEntity = youtubeNotificationLogService.insertLog(video, channel, YouTubeSubscriptionType.LIVE_START);

        notifyForms.parallelStream().forEach(notifyForm -> {
            final Locale locale = Locale.forLanguageTag(notifyForm.getLanguageIsoData().getCode());
            final String embedSuffix = messageSource.getMessage("youtube.stream.event-message", null, locale);
            final String footerMessage = messageSource.getMessage("youtube.stream.footer", null, locale);

            final DiscordEmbed.Webhook discordWebhookMessage = makeDiscordWebhook(video, channel, notifyForm, embedSuffix, footerMessage);
            log.debug("Made Webhook Message: " + discordWebhookMessage);

            final HttpStatus httpStatus = discordWebhookService.send(discordWebhookMessage, notifyForm.getWebhookId().getWebhookUrl());
            youTubeUserLogService.insertUserLog(notifyForm, youTubeNotificationLogEntity, httpStatus.is2xxSuccessful());
        });
    }

    public void sendVideoUploadMessage(Video video, Channel channel) {
        log.info("Send Video Upload Message");

        final List<YouTubeSubscriptionFormEntity> notifyForms = getValidNotificationForms(channel.getId(), YouTubeSubscriptionType.VIDEO_UPLOAD);
        if(notifyForms == null) {
            log.info("Received Null. Finish The Processing");
            return;
        }

        final YouTubeNotificationLogEntity youTubeNotificationLogEntity = youtubeNotificationLogService.insertLog(video, channel, YouTubeSubscriptionType.VIDEO_UPLOAD);

        notifyForms.parallelStream().forEach(notifyForm -> {
            final Locale locale = Locale.forLanguageTag(notifyForm.getLanguageIsoData().getCode());
            final String embedSuffix = messageSource.getMessage("youtube.video.upload.event-message", null, locale);
            final String footerMessage = messageSource.getMessage("youtube.video.upload.footer", null, locale);

            final DiscordEmbed.Webhook discordWebhookMessage = makeDiscordWebhook(video, channel, notifyForm, embedSuffix, footerMessage);
            log.debug("Made Webhook Message: " + discordWebhookMessage);

            final HttpStatus httpStatus = discordWebhookService.send(discordWebhookMessage, notifyForm.getWebhookId().getWebhookUrl());
            youTubeUserLogService.insertUserLog(notifyForm, youTubeNotificationLogEntity, httpStatus.is2xxSuccessful());
        });
    }

    private List<YouTubeSubscriptionFormEntity> getValidNotificationForms(String youtubeChannelId, YouTubeSubscriptionType subscriptionType) {
        final YouTubeChannelInfoEntity youTubeChannelInfoEntity = youTubeChannelInfoRepository.getYouTubeChannelInfoEntityByYoutubeChannelId(youtubeChannelId);
        final List<YouTubeSubscriptionFormEntity> notifyForms = youTubeSubscriptionFormRepository
                .findAllByYouTubeChannelInfoEntityAndYouTubeSubscriptionType(youTubeChannelInfoEntity, subscriptionType);
        log.debug("Received Notify Forms: " + notifyForms);

        if(notifyForms.isEmpty()) {
            log.info("NotifyForms Is Empty. Return Null.");
            return null;
        }

        final List<YouTubeSubscriptionFormEntity> filteredNotifyForms = notifyForms
                .stream()
                .filter(notifyForm -> youTubeUserLogService.isNotInInterval(youTubeChannelInfoEntity.getYoutubeChannelId(), YouTubeSubscriptionType.LIVE_START, notifyForm.getIntervalMinute()))
                .collect(Collectors.toList());

        if(filteredNotifyForms.isEmpty()) {
            log.info("filteredNotifyForms Is Empty. Return Null.");
            return null;
        }

        return filteredNotifyForms;
    }

    private DiscordEmbed.Webhook makeDiscordWebhook(Video video, Channel channel,
                                                    YouTubeSubscriptionFormEntity form,
                                                    String embedSuffix, String footerMessage) {
        // Author Area
        String youtubeUrl = "https://www.youtube.com";
        final String authorURL = youtubeUrl + "/channel/" + channel.getId();
        String authorProfileURL = channel.getSnippet().getThumbnails().getHigh().getUrl();
        if(authorProfileURL == null) {
            authorProfileURL = channel.getSnippet().getThumbnails().getDefault().getUrl();
        }

        final String authorName = channel.getSnippet().getTitle();
        final DiscordEmbed.Author author = new DiscordEmbed.Author(authorName, authorURL, authorProfileURL);

        // Embed Area
        final String embedColor = Integer.toString(form.getDecimalColor());
        final String embedTitle = channel.getSnippet().getTitle() + embedSuffix;
        final String embedDescription = video.getSnippet().getTitle();

        final DiscordEmbed.Footer footer = new DiscordEmbed.Footer(footerMessage, youTubeLogoUrl);
        String imageUrl = video.getSnippet().getThumbnails().getHigh().getUrl();
        if(imageUrl == null) {
            imageUrl = video.getSnippet().getThumbnails().getDefault().getUrl();
        }
        final DiscordEmbed.Image image = new DiscordEmbed.Image(imageUrl, 640, 480);

        final String videoUrl = youtubeUrl + "/watch?v=" + video.getId();

        List<DiscordEmbed> discordEmbeds = new ArrayList<>();
        final DiscordEmbed discordEmbed = DiscordEmbed.builder()
                .author(author)
                .title(embedTitle)
                .url(videoUrl)
                .description(embedDescription)
                .color(embedColor)
                .footer(footer)
                .timestamp(String.valueOf(video.getLiveStreamingDetails().getActualStartTime()))
                .image(image)
                .build();

        discordEmbeds.add(discordEmbed);

        return new DiscordEmbed.Webhook(form.getBotProfileId().getUsername(),
                form.getBotProfileId().getAvatarUrl(), form.getContent(), discordEmbeds);
    }
}
