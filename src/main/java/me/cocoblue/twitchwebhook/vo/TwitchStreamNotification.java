package me.cocoblue.twitchwebhook.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@Getter
@NoArgsConstructor
@Repository
@ToString
public class TwitchStreamNotification {
    @JsonProperty("id")
    private String id;
    @JsonProperty("user_id")
    private String userId;
    @JsonProperty("user_login")
    private String userLogin;
    @JsonProperty("user_name")
    private String userName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private int gameIdInt;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("game_id")
    private String gameId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("game_name")
    private String gameName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("type")
    private String type;
    @JsonProperty("title")
    private String title;
    @JsonProperty("viewer_count")
    private int viewerCount;
    @JsonProperty("started_at")
    private LocalDateTime startedAt;
    @JsonProperty("language")
    private String language;
    @JsonProperty("thumbnail_url")
    private String thumbnailUrl;
    @JsonProperty("tag_ids")
    private ArrayList<String> tagIds;

    public TwitchStreamNotification(String id, String userId, String userLogin, String gameId, String gameName,
                                    String type, String title, int viewerCount, LocalDateTime startedAt, String language,
                                    String thumbnailUrl, ArrayList<String> tagIds) {
        this.id = id;
        this.userId = userId;
        this.userLogin = userLogin;
        if(!gameId.equals("")) {
            this.gameIdInt = Integer.parseInt(gameId);
        }
        this.gameId = gameId;
        this.gameName = gameName;
        this.type = type;
        this.title = title;
        this.viewerCount = viewerCount;
        this.startedAt = startedAt;
        this.language = language;
        this.thumbnailUrl = thumbnailUrl;
        this.tagIds = tagIds;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
        if(!gameId.equals("")) {
            this.gameIdInt = Integer.parseInt(gameId);
        }
    }

    public void setStartedAt(String timeValue) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        this.startedAt = LocalDateTime.parse(timeValue, formatter);
    }
}