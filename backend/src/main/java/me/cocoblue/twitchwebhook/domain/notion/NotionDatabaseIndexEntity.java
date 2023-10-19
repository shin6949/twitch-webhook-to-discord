package me.cocoblue.twitchwebhook.domain.notion;

import lombok.*;
import me.cocoblue.twitchwebhook.data.LanguageIsoData;
import me.cocoblue.twitchwebhook.domain.discord.BotProfileDataEntity;
import me.cocoblue.twitchwebhook.domain.discord.WebhookDataEntity;
import me.cocoblue.twitchwebhook.domain.twitch.BroadcasterIdEntity;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@Entity(name = "notion_database_index")
public class NotionDatabaseIndexEntity {
    @Id
    @Column(name = "database_id_at_notion", nullable = false)
    private String databaseIdAtNotion;

    @ManyToOne()
    @JoinColumn(name="owner_id", foreignKey = @ForeignKey(name="FK_NOTION_DATABASE_INDEX_OWNER_ID"), nullable = false)
    private BroadcasterIdEntity ownerId;

    @ManyToOne()
    @JoinColumn(name="webhook_id", foreignKey = @ForeignKey(name="FK_NOTION_DATABASE_INDEX_WEBHOOK_ID"), nullable = false)
    private WebhookDataEntity webhookId;

    @ManyToOne()
    @JoinColumn(name="profile_id", foreignKey = @ForeignKey(name="FK_NOTION_DATABASE_INDEX_PROFILE_ID"), nullable = false)
    private BotProfileDataEntity profileId;

    @Enumerated(EnumType.STRING)
    @Column(name = "language", nullable = false)
    private LanguageIsoData languageIsoData;

    @Column(name="default_interval_minute", nullable = false, length = 11)
    @ColumnDefault("10")
    private int defaultIntervalMinute;

    @Column(name = "default_color_hex", nullable = false, length = 11)
    private String defaultColorHex;
}
