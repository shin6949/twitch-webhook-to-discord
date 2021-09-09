package me.cocoblue.twitchwebhook.dto.discord;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.cocoblue.twitchwebhook.dto.discord.embed.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiscordEmbed {
    @JsonProperty("author")
    private Author author;
    @JsonProperty("title")
    private String title;
    @JsonProperty("url")
    private String url;
    @JsonProperty("description")
    private String description;
    @JsonProperty("color")
    private String color;
    @JsonProperty("fields")
    private List<Field> fields;
    @JsonProperty("thumbnail")
    private Thumbnail thumbnail;
    @JsonProperty("image")
    private Image image;
    @JsonProperty("footer")
    private Footer footer;
}

