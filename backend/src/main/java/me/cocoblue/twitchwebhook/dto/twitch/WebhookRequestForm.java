package me.cocoblue.twitchwebhook.dto.twitch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.cocoblue.twitchwebhook.dto.twitch.webhook.Condition;
import me.cocoblue.twitchwebhook.dto.twitch.webhook.Transport;
import org.springframework.stereotype.Repository;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Repository
public class WebhookRequestForm {
    @JsonProperty("type")
    private String type;
    @JsonProperty("version")
    private String version;
    @JsonProperty("condition")
    private Condition condition;
    @JsonProperty("transport")
    private Transport transport;

    public WebhookRequestForm(String type, Condition condition, Transport transport) {
        this.type = type;
        this.version = "1";
        this.condition = condition;
        this.transport = transport;
    }
}
