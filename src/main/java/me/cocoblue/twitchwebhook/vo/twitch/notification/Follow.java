package me.cocoblue.twitchwebhook.vo.twitch.notification;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Getter
@Repository
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Follow {
    @JsonProperty("from_id")
    private String fromId;
    @JsonProperty("from_name")
    private String fromName;
    @JsonProperty("to_id")
    private String toId;
    @JsonProperty("to_name")
    private String toName;
    @JsonProperty("followed_at")
    private LocalDateTime followedAt;
}

