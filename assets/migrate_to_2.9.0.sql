DROP TABLE IF EXISTS youtube_channel_info;

CREATE TABLE youtube_channel_info (
  internal_id bigint(20) NOT NULL PRIMARY KEY AUTO_INCREMENT,
  youtube_channel_id varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  meno VARCHAR(1000) default null
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

INSERT INTO youtube_channel_info (youtube_channel_id)
SELECT channel_id AS channel_id
FROM youtube_subscription_form
GROUP BY channel_id;

UPDATE youtube_subscription_form YSF INNER JOIN youtube_channel_info YCI
ON YSF.channel_id = YCI.youtube_channel_id
SET YSF.channel_id = YCI.internal_id;

ALTER TABLE youtube_subscription_form MODIFY channel_id bigint(20);
ALTER TABLE `youtube_subscription_form`
ADD CONSTRAINT `FK_YOUTUBE_SUBSCRIPTION_FORM_CHANNEL_ID` FOREIGN KEY (`channel_id`) REFERENCES `youtube_channel_info` (`internal_id`);

UPDATE youtube_notification_log YNL INNER JOIN youtube_channel_info YCI
ON YNL.channel_id = YCI.youtube_channel_id
SET YNL.channel_id = YCI.internal_id;

ALTER TABLE youtube_notification_log MODIFY channel_id bigint(20);
ALTER TABLE `youtube_notification_log`
ADD CONSTRAINT `FK_YOUTUBE_NOTIFICATION_LOG_CHANNEL_ID` FOREIGN KEY (`channel_id`) REFERENCES `youtube_channel_info` (`internal_id`);

CREATE VIEW youtube_subscription_group_view AS (
SELECT YCI.youtube_channel_id AS youtube_channel_id, YSF.type AS type, YSF.enabled AS enabled
FROM youtube_subscription_form YSF
        INNER JOIN youtube_channel_info YCI
                   ON YSF.channel_id = YCI.internal_id
GROUP BY YCI.youtube_channel_id, YSF.type
);