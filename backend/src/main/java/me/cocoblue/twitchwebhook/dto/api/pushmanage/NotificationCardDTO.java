package me.cocoblue.twitchwebhook.dto.api.pushmanage;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationCardDTO {
    @JsonProperty("form_id")
    private long formId;
    @JsonProperty("profile_image")
    private String profileImage;
    @JsonProperty("nickname")
    private String nickname;
    @JsonProperty("login_id")
    private String loginId;
    @JsonProperty("notification_type")
    private String notificationType;
    @JsonProperty("notification_count")
    private int notificationCount;
    @JsonProperty("latest_notification_time")
    private LocalDateTime latestNotificationTime;
    @JsonProperty("interval_minute")
    private int intervalMinute;
}
