DROP TABLE IF EXISTS `customer_business_units`;
CREATE TABLE `customer_business_units` (
	`_id` INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL,
	`unit_no` TEXT,
	`unit_name` TEXT,
	`customer_no` TEXT,
	`address` TEXT,
	`city` TEXT,
	`post_code` TEXT,
	`contact` TEXT,
	`phone_no` TEXT,
	`primary_alternative_address_no` TEXT,
	`primary_alternative_address_address` TEXT,
	`primary_alternative_address_city` TEXT,
	`primary_alternative_address_post_code` TEXT, 
	`last_action_plan_sync_date` TEXT DEFAULT NULL
);

DROP TABLE IF EXISTS `electronic_card_customer`;
CREATE TABLE `electronic_card_customer` (
	`_id` INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL,
	`customer_id` INTEGER,
	`business_unit_no` TEXT, 
	`item_id` INTEGER,
	`january_qty` REAL,
	`february_qty` REAL,
	`march_qty` REAL,
	`april_qty` REAL,
	`may_qty` REAL,
	`june_qty` REAL,
	`july_qty` REAL,
	`august_qty` REAL,
	`september_qty` REAL,
	`october_qty` REAL,
	`november_qty` REAL,
	`december_qty` REAL,
	`total_sale_qty_current_year` REAL,
	`total_sale_qty_prior_year` REAL,
	`total_turnover_current_year` REAL,
	`total_turnover_prior_year` REAL,
	`sales_line_counts_current_year` REAL,
	`sales_line_counts_prior_year` REAL,
	`last_line_discount` REAL,
	`color` INTEGER DEFAULT 0,
	`entry_type` INTEGER DEFAULT 0,
	`sorting_index` INTEGER DEFAULT 5000,
	`new_sorting_index` INTEGER DEFAULT 5000,
	`sale_per_branch_index` INTEGER DEFAULT 0,
	`created_date` TEXT,
	`created_by` TEXT,
	`updated_date` TEXT,
	`updated_by` TEXT
);
/*
	`item_price` REAL DEFAULT 0,
	`image_link` TEXT,
	`line_turnover` REAL DEFAULT 0,
*/

CREATE TABLE `items_on_promotion` (
	`_id` INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL,
	`item_no` TEXT,
	`branch_code` TEXT,
	`valid_from_date` TEXT,
	`valid_to_date` TEXT,
	`price` REAL,
	`comment` TEXT,
	`discount` REAL
);

CREATE TABLE `action_plan` (
	`_id` INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL,
	`customer_no` TEXT, 
	`business_unit_no` TEXT, 
	`item_no` TEXT, 
	`item_type` INTEGER DEFAULT 0, 
	`line_turnover` REAL
);

ALTER TABLE `items` ADD `min_qty` INTEGER DEFAULT 1;
ALTER TABLE `items` ADD `bom_items` TEXT;
ALTER TABLE `items` ADD `linked_items` TEXT;
ALTER TABLE `items` ADD `has_actions` INTEGER DEFAULT 0;

ALTER TABLE `customers` ADD `has_business_units` INTEGER DEFAULT 0;
ALTER TABLE `customers` ADD `primary_alternative_address_no` TEXT;
ALTER TABLE `customers` ADD `primary_alternative_address_address` TEXT;
ALTER TABLE `customers` ADD `primary_alternative_address_city` TEXT;
ALTER TABLE `customers` ADD `primary_alternative_address_post_code` TEXT;
ALTER TABLE `customers` ADD `last_action_plan_sync_date` TEXT DEFAULT NULL;
ALTER TABLE `customers` ADD `task_count` INTEGER DEFAULT 0;
ALTER TABLE `customers` ADD `customer_color` INTEGER DEFAULT 0;

ALTER TABLE `sale_orders` ADD `svi_artikli_counter` INTEGER DEFAULT 0;

ALTER TABLE `sale_order_lines` ADD `price_other` REAL;
ALTER TABLE `sale_order_lines` ADD `price_type` INTEGER DEFAULT 0;

UPDATE `app_settings` SET app_version='2.0.0', app_sync_warnning_date=datetime('now') WHERE _id=1;