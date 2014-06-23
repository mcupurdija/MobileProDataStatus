ALTER TABLE `customers` ADD `last_ecc_sync_date` TEXT DEFAULT NULL;
DELETE FROM `electronic_card_customer`;

UPDATE `app_settings` SET app_version='1.6.0', app_sync_warnning_date=datetime('now') WHERE _id=1;