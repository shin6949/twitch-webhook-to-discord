ALTER TABLE youtube_channel_info DROP upload_playlist_id;
ALTER TABLE youtube_channel_info DROP last_checked_time;
ALTER TABLE youtube_channel_info ADD meno VARCHAR(1000) default null;