ALTER TABLE `customers` ADD `mobile` TEXT;

UPDATE `app_settings` SET app_version='1.2.1', app_sync_warnning_date=datetime('now') WHERE _id=1;