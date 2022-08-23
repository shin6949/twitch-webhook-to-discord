DROP TABLE IF EXISTS bot_profile_data;
DROP TABLE IF EXISTS webhook_data;
DROP TABLE IF EXISTS user_log;
ALTER TABLE subscription_form DROP bot_profile_id;

CREATE TABLE webhook_data (
  id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  name varchar(500) NOT NULL,
  webhook_url varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  meno TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
  owner_id BIGINT NOT NULL,
  CONSTRAINT FK_WEBHOOK_DATA_OWNER_ID FOREIGN KEY (owner_id) REFERENCES broadcaster_id(id)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

CREATE TABLE bot_profile_data (
  id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  owner_id BIGINT NOT NULL,
  username VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  avatar_url VARCHAR(600) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  CONSTRAINT FK_BOT_PROFILE_DATA_OWNER_ID FOREIGN KEY (owner_id) REFERENCES broadcaster_id(id)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

CREATE TABLE user_log (
  id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  log_owner_id BIGINT NOT NULL,
  log_id BIGINT NOT NULL,
  status boolean NOT NULL default false,
  result TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
  CONSTRAINT FK_USER_LOG_LOG_OWNER FOREIGN KEY (log_owner_id) REFERENCES broadcaster_id(id),
  CONSTRAINT FK_USER_LOG_LOG_ID FOREIGN KEY (log_id) REFERENCES notification_log(id)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

INSERT INTO webhook_data (name, webhook_url, owner_id)
SELECT username AS name, webhook_url, broadcaster_id AS owner_id
FROM subscription_form
GROUP BY webhook_url;

INSERT INTO bot_profile_data (owner_id, username, avatar_url)
SELECT broadcaster_id AS owner_id, username, avatar_url 
FROM subscription_form
GROUP BY avatar_url, username;

ALTER TABLE subscription_form ADD COLUMN bot_profile_id BIGINT NOT NULL default 0;

UPDATE subscription_form SF INNER JOIN bot_profile_data BPD
ON SF.avatar_url = BPD.avatar_url
SET SF.bot_profile_id = BPD.id;