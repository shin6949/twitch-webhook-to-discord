ALTER TABLE bot_profile_data
    CONVERT TO CHARACTER SET utf8mb4
        COLLATE utf8mb4_unicode_ci;

ALTER TABLE broadcaster_id
    CONVERT TO CHARACTER SET utf8mb4
        COLLATE utf8mb4_unicode_ci;

ALTER TABLE notification_log
    CONVERT TO CHARACTER SET utf8mb4
        COLLATE utf8mb4_unicode_ci;

ALTER TABLE subscription_form
    CONVERT TO CHARACTER SET utf8mb4
        COLLATE utf8mb4_unicode_ci;

ALTER TABLE user_log
    CONVERT TO CHARACTER SET utf8mb4
        COLLATE utf8mb4_unicode_ci;

ALTER TABLE webhook_data
    CONVERT TO CHARACTER SET utf8mb4
        COLLATE utf8mb4_unicode_ci;

ALTER TABLE youtube_channel_info
    CONVERT TO CHARACTER SET utf8mb4
        COLLATE utf8mb4_unicode_ci;

ALTER TABLE youtube_notification_log
    CONVERT TO CHARACTER SET utf8mb4
        COLLATE utf8mb4_unicode_ci;

ALTER TABLE youtube_subscription_form
    CONVERT TO CHARACTER SET utf8mb4
        COLLATE utf8mb4_unicode_ci;

ALTER TABLE youtube_user_log
    CONVERT TO CHARACTER SET utf8mb4
        COLLATE utf8mb4_unicode_ci;

DROP TABLE IF EXISTS push_user_log;
DROP TABLE IF EXISTS push_subscription_form;
CREATE TABLE `push_subscription_form` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `broadcaster_id` BIGINT NOT NULL,
  `type` varchar(255) NOT NULL,
  `language` varchar(255) NOT NULL,
  `registration_token` VARCHAR(255) NOT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `interval_minute` INT(11) NOT NULL DEFAULT 10,
  `enabled` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  FOREIGN KEY (`broadcaster_id`) REFERENCES `broadcaster_id`(id)
      ON UPDATE CASCADE
      ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

ALTER TABLE push_subscription_form
    CONVERT TO CHARACTER SET utf8mb4
        COLLATE utf8mb4_unicode_ci;

CREATE TABLE push_user_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    form_id BIGINT NOT NULL,
    log_id BIGINT NOT NULL,
    status tinyint(1) NOT NULL,
    FOREIGN KEY(form_id) REFERENCES push_subscription_form(id)
);

ALTER TABLE `broadcaster_id`
  ADD COLUMN `profile_url` VARCHAR(255),
  ADD COLUMN `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  ADD COLUMN `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

ALTER TABLE subscription_form MODIFY COLUMN enabled TINYINT(1);
ALTER TABLE push_subscription_form MODIFY COLUMN enabled TINYINT(1);

DROP VIEW IF EXISTS subscription_group_view;
CREATE VIEW subscription_group_view AS (
   SELECT broadcaster_id, type, enabled
   FROM (
            SELECT broadcaster_id, type, enabled
            FROM subscription_form
            UNION ALL
            SELECT broadcaster_id, type, enabled
            FROM push_subscription_form
        ) AS combined
   GROUP BY broadcaster_id, type
);

DROP VIEW IF EXISTS push_user_log_view;
CREATE VIEW push_user_log_view AS (
SELECT ul.id as user_log_id, nl.received_time as received_time, nl.type as type, ul.form_id as form_id, nl.broadcaster_id as broadcaster_id
FROM push_user_log ul
      JOIN notification_log nl
           ON nl.id = ul.log_id
 );