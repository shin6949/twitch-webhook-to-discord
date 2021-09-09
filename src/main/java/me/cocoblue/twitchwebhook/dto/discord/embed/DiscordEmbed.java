package me.cocoblue.twitchwebhook.dto.discord.embed;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiscordEmbed {
    @JsonProperty("author")
    private DiscordAuthor author;
    @JsonProperty("title")
    private String title;
    @JsonProperty("url")
    private String url;
    @JsonProperty("description")
    private String description;
    @JsonProperty("color")
    private String color;
    @JsonProperty("fields")
    private List<DiscordField> fields;
    @JsonProperty("thumbnail")
    private DiscordThumbnail thumbnail;
    @JsonProperty("image")
    private DiscordImage image;
    @JsonProperty("footer")
    private DiscordFooter footer;
}

