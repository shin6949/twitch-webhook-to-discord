CREATE TABLE `push_subscription_form` (
  `id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `broadcaster_id` BIGINT NOT NULL,
  `type` varchar(255) NOT NULL,
  `language` varchar(255) NOT NULL,
  `registration_token` VARCHAR(255) NOT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `interval_minute` INT(11) NOT NULL DEFAULT 10,
  `enabled` BIT(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`id`),
  FOREIGN KEY (`broadcaster_id`) REFERENCES `broadcaster_id`(id)
      ON UPDATE CASCADE
      ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

ALTER TABLE `broadcaster_id`
  ADD COLUMN `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  ADD COLUMN `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;