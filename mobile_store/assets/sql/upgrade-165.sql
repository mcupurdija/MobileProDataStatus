ALTER TABLE `visits` ADD `address_no` TEXT;
ALTER TABLE `visits` ADD `latitude` TEXT;
ALTER TABLE `visits` ADD `longitude` TEXT;
ALTER TABLE `visits` ADD `accuracy` TEXT;
ALTER TABLE `visits` ADD `latitude_end` TEXT;
ALTER TABLE `visits` ADD `longitude_end` TEXT;
ALTER TABLE `visits` ADD `accuracy_end` TEXT;
ALTER TABLE `visits` ADD `valid_location` INTEGER DEFAULT 0;

ALTER TABLE `sales_persons` ADD `wr_username` TEXT;
ALTER TABLE `sales_persons` ADD `wr_password` TEXT;

ALTER TABLE `electronic_card_customer` ADD `color` INTEGER DEFAULT 0;
ALTER TABLE `electronic_card_customer` ADD `sorting_index` INTEGER DEFAULT 5000;

UPDATE `app_settings` SET app_version='1.5.0', app_sync_warnning_date=datetime('now') WHERE _id=1;