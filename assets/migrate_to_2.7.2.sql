ALTER TABLE user_log DROP `result`;
ALTER TABLE user_log DROP FOREIGN KEY FK_USER_LOG_LOG_OWNER;
ALTER TABLE user_log DROP `log_owner_id`;
ALTER TABLE user_log ADD COLUMN form_id BIGINT NOT NULL default 1;
ALTER TABLE user_log ADD CONSTRAINT FK_USER_LOG_FORM_ID FOREIGN KEY (form_id) REFERENCES subscription_form (id);
