package me.cocoblue.twitchwebhook.dto.api.uuid;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import me.cocoblue.twitchwebhook.domain.twitch.PushUUIDStorageEntity;

import java.time.LocalDateTime;

@Data
@ToString
@Builder
public class UUIDResponse {
    @JsonProperty("uuid")
    private String uuid;
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    public UUIDResponse(final PushUUIDStorageEntity pushUUIDStorageEntity) {
        this.uuid = pushUUIDStorageEntity.getUuid();
        this.updatedAt = pushUUIDStorageEntity.getUpdatedAt();
    }
}
