DROP TABLE IF EXISTS bot_profile_data;
DROP TABLE IF EXISTS webhook_data;
DROP TABLE IF EXISTS user_log;

CREATE TABLE webhook_data (
  id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  name varchar(500) NOT NULL,
  webhook_url varchar(500) NOT NULL,
  meno TEXT,
  owner_id BIGINT NOT NULL,
  CONSTRAINT FK_WEBHOOK_DATA_OWNER_ID FOREIGN KEY (owner_id) REFERENCES broadcaster_id(id)
);

CREATE TABLE bot_profile_data (
  id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  owner_id BIGINT NOT NULL,
  username VARCHAR(100) NOT NULL,
  avatar_url VARCHAR(600) NOT NULL,
  CONSTRAINT FK_BOT_PROFILE_DATA_OWNER_ID FOREIGN KEY (owner_id) REFERENCES broadcaster_id(id)
);

CREATE TABLE user_log (
  id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  log_owner_id BIGINT NOT NULL,
  log_id BIGINT NOT NULL,
  status boolean NOT NULL default false,
  result TEXT,
  CONSTRAINT FK_USER_LOG_LOG_OWNER FOREIGN KEY (log_owner_id) REFERENCES broadcaster_id(id),
  CONSTRAINT FK_USER_LOG_LOG_ID FOREIGN KEY (log_id) REFERENCES notification_log(id)
);

INSERT INTO webhook_data (name, webhook_url, owner_id)
SELECT username AS name, webhook_url, broadcaster_id AS owner_id
FROM subscription_form
GROUP BY webhook_url;

INSERT INTO bot_profile_data (owner_id, username, avatar_url)
SELECT broadcaster_id AS owner_id, username, avatar_url 
FROM subscription_form
GROUP BY avatar_url, username;

ALTER TABLE subscription_form ADD COLUMN bot_profile_id BIGINT NOT NULL default 0;

UPDATE subscription_form
SET bot_profile_id = (SELECT id 
                        FROM bot_profile_data 
                        WHERE avatar_url = subscription_form.avatar_url
                        AND bot_profile_data.username = subscription_form.username)
