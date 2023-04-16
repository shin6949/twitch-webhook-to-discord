package me.cocoblue.twitchwebhook.dto.twitch.eventsub;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import me.cocoblue.twitchwebhook.dto.twitch.webhook.Condition;
import me.cocoblue.twitchwebhook.dto.twitch.webhook.Transport;

@Data
public class SubscribeRequest {
    @JsonProperty("type")
    private String type;
    @JsonProperty("version")
    private String version;
    @JsonProperty("condition")
    private Condition condition;
    @JsonProperty("transport")
    private Transport transport;

    public SubscribeRequest(String type, Condition condition, Transport transport) {
        this.type = type;
        this.version = "1";
        this.condition = condition;
        this.transport = transport;
    }
}
