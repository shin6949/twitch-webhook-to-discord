DROP TABLE IF EXISTS youtube_subscription_form;
DROP TABLE IF EXISTS youtube_notification_log;

CREATE TABLE youtube_subscription_form (
    id bigint(20) NOT NULL PRIMARY KEY AUTO_INCREMENT,
    channel_id varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    content varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
    bot_profile_id bigint(20) NOT NULL,
    type varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    color_hex varchar(20) NOT NULL,
    language varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    webhook_id bigint(20) NOT NULL,
    form_owner bigint(20) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT now(),
    enabled tinyint(1) NOT NULL DEFAULT false,
    CONSTRAINT FK_YOUTUBE_SUBSCRIPTION_FORM_WEBHOOK_ID FOREIGN KEY (webhook_id) REFERENCES webhook_data(id),
    CONSTRAINT FK_YOUTUBE_SUBSCRIPTION_FORM_OWNER_BROADCASTER_ID FOREIGN KEY (form_owner) REFERENCES broadcaster_id(id),
    CONSTRAINT FK_YOUTUBE_SUBSCRIPTION_FORM_BOT_PROFILE_ID FOREIGN KEY (bot_profile_id) REFERENCES bot_profile_data(id)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

CREATE TABLE youtube_notification_log (
  id bigint(20) NOT NULL PRIMARY KEY AUTO_INCREMENT,
  type varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  channel_id varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  video_id varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  received_time DATETIME NOT NULL DEFAULT now()
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;