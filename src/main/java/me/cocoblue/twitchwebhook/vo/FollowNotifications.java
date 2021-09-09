package me.cocoblue.twitchwebhook.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import me.cocoblue.twitchwebhook.vo.twitch.notification.Follow;
import org.springframework.stereotype.Repository;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Repository
@ToString
public class FollowNotifications {
    @JsonProperty("data")
    private List<Follow> notifications;
}