package me.cocoblue.twitchwebhook.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import me.cocoblue.twitchwebhook.vo.twitch.notification.Stream;
import org.springframework.stereotype.Repository;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Repository
@ToString
public class StreamNotification {
    @JsonProperty("data")
    private List<Stream> notification;
}
