package me.cocoblue.twitchwebhook.dto.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.cocoblue.twitchwebhook.dto.twitch.User;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchResultDTO {
    @JsonProperty("result")
    private boolean result;
    @JsonProperty("is_live")
    private boolean isLive;
    @JsonProperty("user")
    private User user;
}
