package me.cocoblue.twitchwebhook.dto.twitch.eventsub;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.cocoblue.twitchwebhook.dto.twitch.webhook.Condition;
import me.cocoblue.twitchwebhook.dto.twitch.webhook.Transport;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostRequestBody {
    @JsonProperty("type")
    private String type;
    @JsonProperty("version")
    private String version;
    @JsonProperty("condition")
    private Condition condition;
    @JsonProperty("Transport")
    private Transport transport;

    public PostRequestBody(String type, Condition condition, Transport transport) {
        this.type = type;
        this.version = "1";
        this.condition = condition;
        this.transport = transport;
    }
}
