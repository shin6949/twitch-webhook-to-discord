package me.cocoblue.twitchwebhook.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "game_index")
@Builder
public class GameIndexEntity {
    @Id
    private Long id;
    @Column(length = 300)
    private String name;
}
