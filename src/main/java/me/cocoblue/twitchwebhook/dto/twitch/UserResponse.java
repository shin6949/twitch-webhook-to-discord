package me.cocoblue.twitchwebhook.dto.twitch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Repository;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Repository
@Getter
@ToString
public class UserResponse {
    @JsonProperty("data")
    List<User> twitchUsers;
}
