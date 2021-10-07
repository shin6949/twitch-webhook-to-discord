package me.cocoblue.twitchwebhook.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotifyLog {
    private int id;
    private String idFromTwitch;
    private int streamerId;
    private String title;
    private LocalDateTime startedAt;
    private int gameId;
    private LocalDateTime recordTimeToDb;
}
