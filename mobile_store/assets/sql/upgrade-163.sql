
ALTER TABLE `sale_orders` ADD `requested_delivery_date` TEXT DEFAULT NULL;
ALTER TABLE `sale_order_lines` ADD `location_code` TEXT DEFAULT '001';
ALTER TABLE `service_orders` ADD `address_no` TEXT DEFAULT NULL;

CREATE TABLE `app_settings` ( 
	`_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
	`app_version` TEXT,
	`app_sync_warnning_date` TEXT,
	`customers_sync_warnning_date` TEXT,
	`items_sync_warnning_date` TEXT
);

insert into `app_settings` (_id, app_version, app_sync_warnning_date, customers_sync_warnning_date, items_sync_warnning_date) values (1, '1.0.9', datetime('now'), datetime('now'), datetime('now'));