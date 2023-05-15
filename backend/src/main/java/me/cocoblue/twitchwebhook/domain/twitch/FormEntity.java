package me.cocoblue.twitchwebhook.domain.twitch;

import lombok.*;
import lombok.experimental.SuperBuilder;
import me.cocoblue.twitchwebhook.data.LanguageIsoData;
import me.cocoblue.twitchwebhook.data.TwitchSubscriptionType;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
@SuperBuilder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public abstract class FormEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="broadcaster_id", foreignKey = @ForeignKey(name="FK_SUBSCRIPTION_FORM_BROADCASTER_ID"))
    private BroadcasterIdEntity broadcasterIdEntity;

    @Enumerated(EnumType.STRING)
    @Column(name="type", nullable = false)
    private TwitchSubscriptionType twitchSubscriptionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "language", nullable = false)
    private LanguageIsoData languageIsoData;

    @Column(name="created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name="interval_minute", nullable = false, length = 11)
    @ColumnDefault("10")
    private int intervalMinute;

    @Column(name="enabled", nullable = false, columnDefinition = "BIT", length = 1)
    @ColumnDefault("false")
    private boolean enabled;
}