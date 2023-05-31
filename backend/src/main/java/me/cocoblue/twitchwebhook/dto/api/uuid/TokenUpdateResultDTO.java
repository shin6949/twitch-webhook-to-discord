package me.cocoblue.twitchwebhook.dto.api.uuid;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TokenUpdateResultDTO extends UUIDResponse {
    @JsonProperty("is_uuid_modified")
    private boolean isUUIDModified;

    public TokenUpdateResultDTO(UUIDResponse uuidResponse, boolean isUUIDModified) {
        super(uuidResponse.getUuid(), uuidResponse.getUpdatedAt());
        this.setUUIDModified(isUUIDModified);
    }
}
