package me.cocoblue.twitchwebhook.controller.youtube;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.Video;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.data.YouTubeSubscriptionType;
import me.cocoblue.twitchwebhook.dto.youtube.YouTubeXmlBody;
import me.cocoblue.twitchwebhook.service.EncryptDataService;
import me.cocoblue.twitchwebhook.service.youtube.NewVideoNotifyService;
import me.cocoblue.twitchwebhook.service.youtube.NotificationLogService;
import me.cocoblue.twitchwebhook.service.youtube.APIActionService;
import me.cocoblue.twitchwebhook.service.youtube.YouTubeChannelInfoService;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping(path = "/webhook/youtube")
@Log4j2
@AllArgsConstructor
public class YouTubeStreamNotifyController {
    private final EncryptDataService encryptDataService;
    private final APIActionService APIActionService;
    private final NotificationLogService notificationLogService;
    private final NewVideoNotifyService newVideoNotifyService;
    private final YouTubeChannelInfoService youTubeChannelInfoService;

    @GetMapping("/{channelId}")
    public String challengeControl(@PathVariable String channelId,
                                 @RequestParam(name = "hub.challenge") String challengeWord) {
        log.info("Received Challenge. Returning the code.");

        return challengeWord;
    }

    @PostMapping("/{channelId}")
    public String receiveNotification(@PathVariable String channelId, @RequestBody String notification,
                                      @RequestHeader HttpHeaders headers) {
        log.info("YouTube Event Received");
        log.info("Received Channel ID: " + channelId);
        log.debug("@RequestBody: " + notification);
        log.debug("@RequestHeader: " + headers.toString());

        if(dataNotValid(headers, notification)) {
            log.warn("This req is NOT valid. (Encryption Value is not match between both side.) Stop the Processing.");
            return "success";
        }

        final YouTubeXmlBody youTubeXmlBody = toDto(notification);
        if(youTubeXmlBody == null) {
            log.error("toDto Process Failed.");
            return "success";
        }

        // Delete 알림은 아직 지원하지 않음.
        if(youTubeXmlBody.getDeleteEntry() != null) {
            log.info("This Notification is for deleted video. This function is not yet.");
            return "true";
        }

        // 중복 알림인지 판단.
        if(!notificationLogService
                .judgeDuplicateNotification(youTubeXmlBody.getVideoId(), youTubeXmlBody.getChannelId())) {
            log.info("This Notification is duplicated. Stop the processing.");
            return "true";
        }

        final Video video = APIActionService.getVideoInfo(youTubeXmlBody.getVideoId());
        if(video == null) {
            log.info("Video is null. Stop the processing.");
            return "true";
        }
        log.debug("Video: " + video);

        if(video.getSnippet().getLiveBroadcastContent().equals("upcoming")) {
            log.info("Upcoming Live Streaming Detected. Update the Information");
            youTubeChannelInfoService.updateUpcomingLiveIdByYoutubeChannelId(video.getId(), channelId);
            return "true";
        }

        final Channel channel = APIActionService.getChannelInfo(youTubeXmlBody.getChannelId());
        log.debug("Channel: " + channel);

        if(video.getSnippet().getLiveBroadcastContent().equals("live")) {
            log.info("Live Streaming Detected. Send the notification");
            newVideoNotifyService.sendLiveStreamMessage(video, channel);
            return "true";
        }

        if(channel == null) {
            log.info("Channel is null. Stop the processing.");
            return "true";
        }

        log.info("New video upload Detected. Send the notification");
        newVideoNotifyService.sendVideoUploadMessage(video, channel);
        notificationLogService.insertLog(video, channel, YouTubeSubscriptionType.VIDEO_UPLOAD);
        return "true";
    }

    private boolean dataNotValid(HttpHeaders headers, String notification) {
        final String signature = Objects.requireNonNull(headers.get("X-Hub-Signature")).get(0);

        final String encryptValue = "sha1=" + encryptDataService.encryptString(notification, false);
        log.debug("Received Signature: " + signature);
        log.debug("Encrypt Value: " + encryptValue);

        return !encryptValue.equals(signature);
    }

    private YouTubeXmlBody toDto(String requestBody) {
        try {
            XmlMapper xmlMapper = new XmlMapper();
            return xmlMapper.readValue(requestBody, YouTubeXmlBody.class);
        } catch (JsonProcessingException jsonProcessingException) {
            jsonProcessingException.printStackTrace();
            return null;
        }
    }
}
