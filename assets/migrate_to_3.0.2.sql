CREATE TABLE `push_uuid_storage` (
 `uuid` VARCHAR(255) NOT NULL PRIMARY KEY,
 `fcm_token` VARCHAR(300) NOT NULL UNIQUE,
 `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO push_uuid_storage (uuid, fcm_token)
SELECT UUID(), registration_token
FROM push_subscription_form
GROUP BY registration_token;

ALTER TABLE `push_subscription_form`
    ADD COLUMN `registration_uuid` VARCHAR(255) NOT NULL;

UPDATE push_subscription_form psf
    INNER JOIN push_uuid_storage us ON psf.registration_token = us.fcm_token
SET psf.registration_uuid = us.uuid;

ALTER TABLE `push_subscription_form`
    ADD CONSTRAINT `FK_push_subscription_form_registration_uuid`
        FOREIGN KEY (`registration_uuid`) REFERENCES `push_uuid_storage`(`uuid`)
            ON UPDATE CASCADE
            ON DELETE CASCADE;

ALTER TABLE `push_subscription_form` DROP COLUMN `registration_token`;