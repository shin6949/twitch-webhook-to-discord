package me.cocoblue.twitchwebhook.dto.api.uuid;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class UUIDRequestDTO {
    @JsonProperty("uuid")
    private String uuid;
    @JsonProperty("fcm_token")
    private String fcmToken;
}
