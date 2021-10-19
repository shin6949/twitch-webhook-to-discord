package me.cocoblue.twitchwebhook.entity;

import lombok.*;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "broadcaster_id")
@Builder
public class BroadcasterId {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 300)
    private String loginId;
    @Column(length = 300)
    private String displayName;
}
