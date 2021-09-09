package me.cocoblue.twitchwebhook.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Repository
@Getter
@ToString
public class TwitchUser {
    @JsonProperty("id")
    private int id;
    @JsonProperty("login")
    private String login;
    @JsonProperty("display_name")
    private String displayName;
    @JsonProperty("type")
    private String type;
    @JsonProperty("broadcaster_type")
    private String broadcasterType;
    @JsonProperty("description")
    private String description;
    @JsonProperty("profile_image_url")
    private String profileImageUrl;
    @JsonProperty("offline_image_url")
    private String offlineImageUrl;
    @JsonProperty("view_count")
    private int viewCount;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("email")
    private String email;
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
