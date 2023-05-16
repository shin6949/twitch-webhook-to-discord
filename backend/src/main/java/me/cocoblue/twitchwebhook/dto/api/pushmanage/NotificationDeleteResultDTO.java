package me.cocoblue.twitchwebhook.dto.api.pushmanage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDeleteResultDTO {
    private boolean result;
    private String message;
}
