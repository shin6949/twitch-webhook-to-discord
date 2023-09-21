package me.cocoblue.twitchwebhook.domain.notion;

import lombok.*;
import me.cocoblue.twitchwebhook.domain.discord.WebhookDataEntity;
import me.cocoblue.twitchwebhook.domain.twitch.BroadcasterIdEntity;

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

    @Column(name = "default_color_hex", nullable = false, length = 11)
    private String defaultColorHex;
}
