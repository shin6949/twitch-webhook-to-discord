package me.cocoblue.twitchwebhook.domain.discord;

import com.sun.istack.NotNull;
import lombok.*;
import me.cocoblue.twitchwebhook.domain.twitch.BroadcasterIdEntity;

import javax.persistence.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@Entity(name = "bot_profile_data")
public class BotProfileDataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne()
    @JoinColumn(name="owner_id", foreignKey = @ForeignKey(name="FK_BOT_PROFILE_DATA_OWNER_ID"))
    @NotNull
    private BroadcasterIdEntity ownerId;

    @Column(length = 100)
    @NotNull
    private String username;

    @Column(length = 600)
    @NotNull
    private String avatarUrl;
}
