package me.cocoblue.twitchwebhook.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import me.cocoblue.twitchwebhook.vo.twitch.notification.UserChange;
import org.springframework.stereotype.Repository;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Repository
@Getter
@ToString
public class UserChangeNotifications {
    @JsonProperty("data")
    private List<UserChange> notifications;
}

