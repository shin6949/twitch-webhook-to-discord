package me.cocoblue.twitchwebhook.domain.youtube;

import com.sun.istack.NotNull;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "channel_id", length = 150)
    @NotNull
    private String channelId;

    @Column(name = "upload_playlist_id", length = 150)
    @NotNull
    private String uploadPlaylistId;
}
