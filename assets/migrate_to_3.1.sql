CREATE TABLE notion_database_index (
    database_id_at_notion VARCHAR(255) NOT NULL PRIMARY KEY,
    owner_id BIGINT,
    webhook_id BIGINT,
    default_color VARCHAR(11) NOT NULL,
    FOREIGN KEY (owner_id) REFERENCES broadcaster_id(id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (webhook_id) REFERENCES webhook_id(id) ON UPDATE CASCADE ON DELETE CASCADE
)