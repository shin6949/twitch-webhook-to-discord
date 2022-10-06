DROP TABLE IF EXISTS youtube_subscription_form;

CREATE TABLE youtube_subscription_form (
    id bigint(20) NOT NULL PRIMARY KEY AUTO_INCREMENT,
    channel_id varchar(100) NOT NULL,
    content varchar(2000) NULL DEFAULT NULL,
    type varchar(255) NOT NULL,
    color_hex varchar(20) NOT NULL,
    language varchar(255) NOT NULL,
    webhook_id bigint(20) NOT NULL,
    form_owner bigint(20) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT now(),
    enabled tinyint(1) NOT NULL DEFAULT false,
    CONSTRAINT FK_YOUTUBE_SUBSCRIPTION_FORM_WEBHOOK_ID FOREIGN KEY (webhook_id) REFERENCES webhook_data(id),
    CONSTRAINT FK_YOUTUBE_SUBSCRIPTION_FORM_OWNER_BROADCASTER_ID FOREIGN KEY (form_owner) REFERENCES broadcaster_id(id)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;