package me.cocoblue.twitchwebhook.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Repository;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameIndex {
    private int id;
    private String name;
}
