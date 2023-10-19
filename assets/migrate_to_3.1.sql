CREATE TABLE notion_database_index (
    database_id_at_notion VARCHAR(255) NOT NULL PRIMARY KEY,
    owner_id BIGINT,
    webhook_id BIGINT,
    profile_id BIGINT,
    default_language VARCHAR(255) NOT NULL,
    default_interval_minute INT NOT NULL DEFAULT 10,
    default_color_hex VARCHAR(11) NOT NULL,
    FOREIGN KEY (profile_id) REFERENCES bot_profile_data(id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (owner_id) REFERENCES broadcaster_id(id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (webhook_id) REFERENCES webhook_data(id) ON UPDATE CASCADE ON DELETE CASCADE
);