ALTER TABLE `youtube_channel_info` ADD `upcoming_live_id` varchar(150) DEFAULT null AFTER `upload_playlist_id`;

DROP VIEW `youtube_subscription_group_view`;
CREATE VIEW youtube_subscription_group_view AS (
    SELECT YCI.youtube_channel_id AS youtube_channel_id, YSF.type AS type, YSF.enabled AS enabled
    FROM youtube_subscription_form YSF
        INNER JOIN youtube_channel_info YCI
       ON YSF.channel_id = YCI.internal_id
    GROUP BY YCI.youtube_channel_id, YSF.type
);