package me.cocoblue.twitchwebhook.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestLog {
    private int id;
    private int broadcasterId;
    private String challenge;
    private LocalDateTime requestTime;

    public RequestLog(int broadcasterId, String challenge) {
        this.broadcasterId = broadcasterId;
        this.challenge = challenge;
    }
}
