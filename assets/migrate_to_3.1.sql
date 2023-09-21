CREATE TABLE notion_database_index (
    database_id_at_notion VARCHAR(255) NOT NULL PRIMARY KEY,
    owner_id BIGINT NOT NULL,
    default_color VARCHAR(11) NOT NULL
)