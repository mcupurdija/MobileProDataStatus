ALTER TABLE `customers` ADD `customer_link` TEXT DEFAULT NULL;

UPDATE `app_settings` SET app_version='1.1.0', app_sync_warnning_date=datetime('now') WHERE _id=1;