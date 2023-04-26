package me.cocoblue.twitchwebhook.dto.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.cocoblue.twitchwebhook.data.TwitchSubscriptionType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationTypeDTO {
    private String value;
    private String name;

    public NotificationTypeDTO(TwitchSubscriptionType twitchSubscriptionType) {
        this.value = twitchSubscriptionType.getTwitchName();
        this.name = twitchSubscriptionType.getKoreanName();
    }
}
