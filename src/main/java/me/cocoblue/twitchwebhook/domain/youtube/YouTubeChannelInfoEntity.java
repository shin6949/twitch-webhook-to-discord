package me.cocoblue.twitchwebhook.domain.youtube;

import com.sun.istack.NotNull;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@DynamicInsert
@Entity(name = "youtube_channel_info")
public class YouTubeChannelInfoEntity {
    @Id
    @Column(name = "internal_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long internalId;

    @Column(name = "youtube_channel_id", length = 150)
    @NotNull
    private String youtubeChannelId;

    @Column(name = "upload_playlist_id", length = 150)
    private String uploadPlaylistId;

    @Column(name = "upcoming_live_id", length = 150)
    private String upcomingLiveId;

    @Column(name = "last_checked_time")
    private LocalDateTime lastCheckedTime;
}
