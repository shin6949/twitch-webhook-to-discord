package me.cocoblue.twitchwebhook.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "oauth_token")
@Builder
public class OauthTokenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 100)
    private String accessToken;
    @Column(length = 100)
    private String refreshToken;
    private int expire;
    private LocalDateTime createDate;
}
