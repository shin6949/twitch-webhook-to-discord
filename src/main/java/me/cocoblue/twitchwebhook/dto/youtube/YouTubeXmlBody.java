package me.cocoblue.twitchwebhook.dto.youtube;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

@JsonRootName("feed")
@Data
public class YouTubeXmlBody {
    @JacksonXmlProperty(localName = "deleted-entry")
    private DeleteEntry deleteEntry;

    @JacksonXmlProperty(localName = "entry")
    private Entry entry;

    @JacksonXmlProperty(localName = "title")
    private String title;

    @JacksonXmlProperty(localName = "updated")
    private String updatedString;

    @JacksonXmlProperty(localName = "link")
    private String link;
}

@Data
class DeleteEntry {
    @JacksonXmlProperty(localName = "ref", isAttribute = true)
    private String refText;

    @JacksonXmlProperty(localName = "when", isAttribute = true)
    private String when;

    @JacksonXmlProperty(localName = "link")
    private String link;

    @JacksonXmlProperty(localName = "by")
    private By by;
}

@Data
@JsonRootName("by")
class By {
    @JsonProperty("name")
    private String name;

    @JsonProperty("uri")
    private String uri;
}

@Data
@JacksonXmlRootElement(localName = "entry")
class Entry {
    @JacksonXmlProperty(localName = "id")
    private String id;

    @JacksonXmlProperty(localName = "videoId")
    private String videoId;

    @JacksonXmlProperty(localName = "channelId")
    private String channelId;

    @JacksonXmlProperty(localName = "title")
    private String title;

    @JacksonXmlProperty(localName = "author")
    private Author author;

    @JacksonXmlProperty(localName = "published")
    private String publishedString;

    @JacksonXmlProperty(localName = "updated")
    private String updatedString;

    @JacksonXmlProperty(localName = "link")
    private String link;
}

@Data
@JacksonXmlRootElement(localName = "author")
class Author {
    @JacksonXmlProperty(localName = "name")
    private String name;

    @JacksonXmlProperty(localName = "uri")
    private String uri;
}

