package me.cocoblue.twitchwebhook.dto.api.register;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationTypeDTO {
    private String value;
    private String name;
}
