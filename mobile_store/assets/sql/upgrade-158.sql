-- upgrade older 156 version to 157
-- this is first upgrade file
-- date 18.4.2013.

CREATE TABLE `service_orders` ( 
	`_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
	`service_order_no` TEXT,
	`customer_id` INTEGER,
	`item_id` INTEGER,
	`address` TEXT,
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