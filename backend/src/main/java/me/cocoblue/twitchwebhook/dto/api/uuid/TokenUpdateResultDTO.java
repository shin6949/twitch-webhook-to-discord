package me.cocoblue.twitchwebhook.dto.api.uuid;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TokenUpdateResultDTO extends UUIDResponse {
    @JsonProperty("is_uuid_modified")
    private boolean isUUIDModified;
}
