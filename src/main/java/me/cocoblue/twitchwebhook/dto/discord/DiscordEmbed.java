package me.cocoblue.twitchwebhook.dto.discord;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscordEmbed {
    @JsonProperty("author")
    private DiscordEmbed.Author author;
    @JsonProperty("title")
    private String title;
    @JsonProperty("url")
    private String url;
    @JsonProperty("description")
    private String description;
    @JsonProperty("color")
    private String color;
    @JsonProperty("fields")
    private List<DiscordEmbed.Field> fields;
    @JsonProperty("thumbnail")
    private Thumbnail thumbnail;
    @JsonProperty("image")
    private Image image;
    @JsonProperty("footer")
    private DiscordEmbed.Footer footer;
    @JsonProperty("timestamp")
    private String timestamp;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Author {
        @JsonProperty("name")
        private String name;
        @JsonProperty("url")
        private String url;
        @JsonProperty("icon_url")
        private String iconUrl;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Field {
        @JsonProperty("name")
        private String name;
        @JsonProperty("value")
        private String value;
        @JsonProperty("inline")
        private Boolean inline;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Footer {
        @JsonProperty("text")
        private String text;
        @JsonProperty("icon_url")
        private String iconUrl;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Webhook {
        @JsonProperty("username")
        private String username;
        @JsonProperty("avatar_url")
        private String avatarUrl;
        @JsonProperty("content")
        private String content;
        @JsonProperty("embeds")
        private List<DiscordEmbed> embeds;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Thumbnail {
        @JsonProperty("url")
        private String url;
    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class Image {
    @JsonProperty("url")
    private String url;
}
