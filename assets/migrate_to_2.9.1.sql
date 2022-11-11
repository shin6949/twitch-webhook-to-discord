ALTER TABLE subscription_form ADD interval_minute int(11) NOT NULL DEFAULT 10 AFTER created_at;
ALTER TABLE youtube_subscription_form ADD interval_minute int(11) NOT NULL DEFAULT 10 AFTER created_at;

UPDATE youtube_subscription_form SET interval_minute = 360 WHERE type = "VIDEO_UPLOAD";

CREATE TABLE youtube_user_log (
  id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  form_id BIGINT NOT NULL,
  log_id BIGINT NOT NULL,
  status boolean NOT NULL default false,
  CONSTRAINT FK_YOUTUBE_USER_LOG_LOG_ID FOREIGN KEY (log_id) REFERENCES youtube_notification_log(id),
  CONSTRAINT FK_YOUTUBE_USER_LOG_FORM_ID FOREIGN KEY (form_id) REFERENCES youtube_subscription_form(id)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

CREATE VIEW user_log_view AS (
    SELECT ul.id as user_log_id, nl.received_time as received_time, nl.type as type, ul.form_id as form_id, nl.broadcaster_id as broadcaster_id
    FROM user_log ul
    JOIN notification_log nl
    ON nl.id = ul.log_id
);

CREATE VIEW youtube_user_log_view AS (
    SELECT yul.id as user_log_id, ynl.received_time as received_time, ynl.type as type, yul.form_id as form_id, ynl.channel_id as channel_id
    FROM youtube_user_log yul
    JOIN youtube_notification_log ynl
    ON ynl.id = yul.log_id
);