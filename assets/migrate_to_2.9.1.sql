ALTER TABLE subscription_form ADD avoid_duplicate_suspicion_noti tinyint(1) NOT NULL DEFAULT 1 AFTER created_at;
ALTER TABLE youtube_subscription_form ADD avoid_duplicate_suspicion_noti tinyint(1) NOT NULL DEFAULT 1 AFTER created_at;

ALTER TABLE youtube_notification_log ADD is_duplicate_suspicion tinyint(1) NOT NULL DEFAULT 1 AFTER received_time;
ALTER TABLE notification_log ADD is_duplicate_suspicion tinyint(1) NOT NULL DEFAULT 1 AFTER received_time;