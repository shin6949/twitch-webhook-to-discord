-- 테스트용 시작
ALTER TABLE subscription_form DROP FOREIGN KEY FK_SUBSCRIPTION_FORM_BOT_PROFILE_ID;
ALTER TABLE subscription_form DROP FOREIGN KEY FK_SUBSCRIPTION_FORM_WEBHOOK_ID;
ALTER TABLE subscription_form DROP FOREIGN KEY FK_SUBSCRIPTION_FORM_FORM_OWNER;
ALTER TABLE subscription_form DROP bot_profile_id;
ALTER TABLE subscription_form DROP form_owner;
DROP VIEW subscription_group_view;
DROP TABLE IF EXISTS bot_profile_data;
DROP TABLE IF EXISTS webhook_data;
DROP TABLE IF EXISTS user_log;
DROP TABLE IF EXISTS subscription_form;
DROP TABLE IF EXISTS notification_log;
DROP TABLE IF EXISTS broadcaster_id;

CREATE TABLE IF NOT EXISTS broadcaster_id SELECT * FROM twitch.broadcaster_id;
CREATE TABLE IF NOT EXISTS notification_log SELECT * FROM twitch.notification_log;
CREATE TABLE IF NOT EXISTS subscription_form SELECT * FROM twitch.subscription_form;
ALTER TABLE `broadcaster_id` ADD PRIMARY KEY (`id`);
ALTER TABLE `notification_log`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK_NOTIFICATION_LOG_BROADCASTER_ID` (`broadcaster_id`);
ALTER TABLE `subscription_form`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK_SUBSCRIPTION_FORM_BROADCASTER_ID` (`broadcaster_id`);
ALTER TABLE `notification_log`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;
ALTER TABLE `subscription_form`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;
ALTER TABLE `notification_log`
  ADD CONSTRAINT `FK_NOTIFICATION_LOG_BROADCASTER_ID` FOREIGN KEY (`broadcaster_id`) REFERENCES `broadcaster_id` (`id`);
ALTER TABLE `subscription_form`
  ADD CONSTRAINT `FK_SUBSCRIPTION_FORM_BROADCASTER_ID` FOREIGN KEY (`broadcaster_id`) REFERENCES `broadcaster_id` (`id`);
-- 테스트용 끝

DROP TABLE IF EXISTS bot_profile_data;
DROP TABLE IF EXISTS webhook_data;
DROP TABLE IF EXISTS user_log;

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

-- Bot Profile Column 추가
ALTER TABLE subscription_form ADD COLUMN bot_profile_id BIGINT NOT NULL default 0;
-- Bot Profile 데이터 반영
UPDATE subscription_form SF INNER JOIN bot_profile_data BPD
ON SF.avatar_url = BPD.avatar_url
SET SF.bot_profile_id = BPD.id;
-- Bot Profile 제약 조건 설정
ALTER TABLE subscription_form ADD CONSTRAINT FK_SUBSCRIPTION_FORM_BOT_PROFILE_ID
FOREIGN KEY (bot_profile_id)
REFERENCES bot_profile_data (id);

-- webhook_id Column 추가
ALTER TABLE subscription_form ADD COLUMN webhook_id BIGINT NOT NULL default 0;
-- Webhook Data 데이터 반영
UPDATE subscription_form SF INNER JOIN webhook_data WD
ON SF.webhook_url = WD.webhook_url
SET SF.webhook_id = WD.id;
-- Webhook Data 제약 조건 설정
ALTER TABLE subscription_form ADD CONSTRAINT FK_SUBSCRIPTION_FORM_WEBHOOK_ID
FOREIGN KEY (webhook_id)
REFERENCES webhook_data (id);

-- Form Owner 추가
ALTER TABLE subscription_form ADD COLUMN form_owner BIGINT NOT NULL;
-- Form Owner 데이터 반영
UPDATE subscription_form SF INNER JOIN webhook_data WD
ON SF.webhook_url = WD.webhook_url
SET SF.form_owner = WD.owner_id;
-- Form Owner 제약 조건 설정
ALTER TABLE subscription_form ADD CONSTRAINT FK_SUBSCRIPTION_FORM_FORM_OWNER
FOREIGN KEY (form_owner)
REFERENCES broadcaster_id (id);


ALTER TABLE subscription_form ADD COLUMN enabled Boolean NOT NULL default false;
UPDATE subscription_form SET enabled = 1;

ALTER TABLE subscription_form ADD COLUMN created_at DATETIME NOT NULL default now();
UPDATE subscription_form SET created_at = '2022-06-01 00:00:00';

CREATE VIEW subscription_group_view AS (
  SELECT broadcaster_id, type, enabled
  FROM subscription_form
  GROUP BY broadcaster_id, type
);

-- 불필요 데이터 제거
ALTER TABLE subscription_form DROP avatar_url;
ALTER TABLE subscription_form DROP username;
ALTER TABLE subscription_form DROP webhook_url;