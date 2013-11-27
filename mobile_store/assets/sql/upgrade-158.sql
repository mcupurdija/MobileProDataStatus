-- upgrade older 157 version to 158
-- date 18.4.2013.

CREATE TABLE `service_orders` ( 
	`_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
	`service_order_no` TEXT,
	`customer_id` INTEGER,
	`item_id` INTEGER,
	`address` TEXT,
	`post_code` TEXT,
	`phone_no` TEXT,
	`quantity_for_reclamation` REAL,
	`note` TEXT,
	`created_date` TEXT,
	`created_by` TEXT,
	`updated_date` TEXT,
	`updated_by` TEXT
);

CREATE TRIGGER IF NOT EXISTS "log_new_service_order" AFTER INSERT ON "service_orders" 
BEGIN 
	update service_orders set 
	created_date = datetime('now'),
	created_by = (select username from users where active = 1)
	where _id = new._id; 
END;
CREATE TRIGGER IF NOT EXISTS "log_service_order_change" AFTER UPDATE ON "service_orders" 
BEGIN 
	update service_orders set 
	updated_date = datetime('now'),
	updated_by = (select username from users where active = 1)
	where _id = new._id; 
END;

-- add signals to customer table
ALTER TABLE `customers` ADD `is_active` INTEGER DEFAULT 1;
ALTER TABLE `customers` ADD `is_deleted` INTEGER DEFAULT 0;
ALTER TABLE `customers` ADD `is_sent` INTEGER DEFAULT 0;

ALTER TABLE `sale_orders` ADD `min_max_discount_total_amount_difference` REAL DEFAULT 0;

ALTER TABLE `items` ADD `inventory_item_category` INTEGER DEFAULT 0;

ALTER TABLE `visits` ADD `entry_subtype` INTEGER DEFAULT 0;