package me.cocoblue.twitchwebhook.domain.discord;

import lombok.*;
import me.cocoblue.twitchwebhook.domain.twitch.BroadcasterIdEntity;

import javax.persistence.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "webhook_data")
@Builder
public class WebhookDataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500, nullable = false)
    private String name;

    @Column(length = 500, nullable = false)
    private String webhookUrl;

    @Column(columnDefinition = "TEXT")
    private String meno;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="owner_id", foreignKey = @ForeignKey(name="FK_WEBHOOK_DATA_OWNER_ID"), nullable = false)
    private BroadcasterIdEntity ownerId;
}
