package me.cocoblue.twitchwebhook.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Repository;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Form {
    private int id;
    private int broadcasterId;
    private String username;
    private String avatarUrl;
    private String content;
    private int color;
    private String type;
    private String webhookUrl;
}
