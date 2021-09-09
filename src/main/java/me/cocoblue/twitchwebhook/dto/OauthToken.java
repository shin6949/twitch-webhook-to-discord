package me.cocoblue.twitchwebhook.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Repository
public class OauthToken {
    private int id;
    private String accessToken;
    private String refreshToken;
    private int expire;
    private LocalDateTime createDate;
}
