package me.cocoblue.twitchwebhook.domain.youtube;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import me.cocoblue.twitchwebhook.data.YouTubeSubscriptionType;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Immutable
@Table(name = "youtube_subscription_group_view")
public class YouTubeSubscriptionGroupViewEntity {
    @EmbeddedId
    private YouTubeSubscriptionGroupViewId youTubeSubscriptionGroupViewId;

    @Enumerated(EnumType.STRING)
    @Column(name="type")
    private YouTubeSubscriptionType youTubeSubscriptionType;

    @Column(name = "upload_playlist_id")
    private String uploadPlayListId;

    @Column(name = "last_checked_time")
    private LocalDateTime lastCheckedTime;

    @Column(name="enabled", nullable = false, columnDefinition = "BIT", length = 1)
    private boolean enabled;

    public String getYouTubeChannelId() {
        return getYouTubeSubscriptionGroupViewId().getYoutubeChannelId();
    }
}

@Data
@Embeddable
class YouTubeSubscriptionGroupViewId implements Serializable {
    @Column(name = "youtube_channel_id")
    private String youtubeChannelId;
}
