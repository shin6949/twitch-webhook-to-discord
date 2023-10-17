package me.cocoblue.twitchwebhook.dto.twitch.eventsub;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StreamNotifyRequestEvent implements NotificationEvent {
    @JsonProperty("broadcaster_user_id")
    private String broadcasterUserId;
    @JsonProperty("broadcaster_user_login")
    private String broadcasterUserLogin;
    @JsonProperty("broadcaster_user_name")
    private String broadcasterUserName;
    @JsonProperty("type")
    private String type;
    @JsonProperty("started_at")
    private LocalDateTime startedAt;

    public void setStartedAt(final String startedAtString) {
        // TEST Case에는 소수점이 있으므로 삭제.
        final int dotIndex = startedAtString.indexOf('.');
        String cutString;
        String timePattern;

        if(dotIndex > 0) {
            cutString = startedAtString.substring(0, dotIndex);
            timePattern = "yyyy-MM-dd'T'HH:mm:ss";
        } else {
            cutString = startedAtString;
            timePattern = "yyyy-MM-dd'T'HH:mm:ss'Z'";
        }

        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(timePattern)
                .withResolverStyle(ResolverStyle.LENIENT);

        this.startedAt = LocalDateTime.parse(cutString, formatter);
    }
}
