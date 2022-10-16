package me.cocoblue.twitchwebhook.domain.youtube;

import lombok.Data;
import lombok.Getter;
import me.cocoblue.twitchwebhook.data.YouTubeSubscriptionType;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Immutable
@Table(name = "youtube_scription_group_view")
public class YouTubeSubscriptionGroupViewEntity {
    @EmbeddedId
    private YouTubeSubscriptionGroupViewId youTubeSubscriptionGroupViewId;

    @Column(name="enabled", nullable = false, columnDefinition = "BIT", length = 1)
    private boolean enabled;

    public String getChannelID() {
        return getYouTubeSubscriptionGroupViewId().getChannelId();
    }

    public YouTubeSubscriptionType getYouTubeSubscriptionType() {
        return getYouTubeSubscriptionGroupViewId().getYouTubeSubscriptionType();
    }
}

@Data
@Embeddable
class YouTubeSubscriptionGroupViewId implements Serializable {
    @Column(name = "channel_id")
    private String channelId;

    @Enumerated(EnumType.STRING)
    @Column(name="type")
    private YouTubeSubscriptionType youTubeSubscriptionType;
}
