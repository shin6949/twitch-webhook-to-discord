package me.cocoblue.twitchwebhook.dto.twitch.eventsub;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.cocoblue.twitchwebhook.dto.twitch.webhook.Condition;
import me.cocoblue.twitchwebhook.dto.twitch.webhook.Transport;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Subscription {
    @JsonProperty("id")
    private String id;
    @JsonProperty("status")
    private String status;
    @JsonProperty("type")
    private String type;
    @JsonProperty("version")
    private String version;
    @JsonProperty("cost")
    private int cost;
    @JsonProperty("condition")
    private Condition condition;
    @JsonProperty("transport")
    private Transport transport;
    @JsonProperty("created_at")
    private String createdAtString;
    private LocalDateTime createdAt;

    public void setCreatedAtString(String createdAtString) {
        this.createdAtString = createdAtString;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS'Z'");
        this.createdAt = LocalDateTime.parse(createdAtString, formatter);
    }
}
