ALTER TABLE `electronic_card_customer` ADD `entry_type` INTEGER DEFAULT 0;

UPDATE `app_settings` SET app_version='1.8.0', app_sync_warnning_date=datetime('now') WHERE _id=1;