package me.cocoblue.twitchwebhook.domain;

import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@Entity(name = "bot_profile_data_entity")
public class BotProfileDataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
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
