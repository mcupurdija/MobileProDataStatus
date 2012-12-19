-- users
CREATE TABLE `users` ( 
	`_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
	`username` TEXT,
	`pass` TEXT,
	`sales_person_id` INTEGER,
	`active` INTEGER NOT NULL  DEFAULT 0,
	`last_login` TEXT
);
INSERT INTO `users` (_id, username, pass, sales_person_id, last_login) VALUES 
	(1, 'tica', 'tica', 1, datetime('now'));
	
-- sales persons
CREATE TABLE `sales_persons` ( 
	`_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
	`sales_person_no` TEXT,
	`name` TEXT,
	`name2` TEXT,
	`created_date` TEXT,
	`created_by` TEXT,
	`updated_date` TEXT,
	`updated_by` TEXT
);
CREATE TRIGGER IF NOT EXISTS "log_new_person" AFTER INSERT ON "sales_persons" 
BEGIN 
	update sales_persons set 
	created_date = datetime('now'),
	created_by = (select username from users where active = 1)
	where _id = new._id; 
END;
CREATE TRIGGER IF NOT EXISTS "log_person_change" AFTER UPDATE ON "sales_persons" 
BEGIN 
	update sales_persons set 
	updated_date = datetime('now'),
	updated_by = (select username from users where active = 1)
	where _id = new._id; 
END;
INSERT INTO `sales_persons` (_id, sales_person_no, name, name2, created_date, created_by) VALUES 
	(1, 'V.MAKEVIC', 'vlada', 'vlada', datetime('now'), 'INITIAL');
-- invoices
CREATE TABLE `invoices` (
	`_id` INTEGER PRIMARY KEY  NOT NULL ,
	`invoice_no` TEXT NOT NULL  DEFAULT (0) ,
	`customer_id` INTEGER,
	`posting_date` TEXT,
	`sales_person_id` INTEGER,
	`due_date` TEXT,
	`total` REAL,
	`total_left` REAL,
	`due_date_days_left` INTEGER,
	`created_date` TEXT,
	`created_by` TEXT,
	`updated_date` TEXT,
	`updated_by`TEXT
);
CREATE TRIGGER IF NOT EXISTS "log_new_invoice" AFTER INSERT ON "invoices" 
BEGIN 
	update invoices set 
	created_date = datetime('now'),
	created_by = (select username from users where active = 1)
	where _id = new._id; 
END;
CREATE TRIGGER IF NOT EXISTS "log_invoice_change" AFTER UPDATE ON "invoices" 
BEGIN 
	update invoices set 
	updated_date = datetime('now'),
	updated_by = (select username from users where active = 1)
	where _id = new._id; 
END;
INSERT INTO `invoices` (`_id` ,	`invoice_no`  ,	`customer_id`,`posting_date` ,`sales_person_id`,`due_date` ,`total` ,`total_left` ,	`due_date_days_left` ,`created_date` ,`created_by` ,`updated_date`,	`updated_by`) VALUES
	(1 , '2012-12-12_NO_1', 12 , '2012-11-10 08:08:00', 1 , '2012-12-12', 1222 , 100 , 5 , '2012-11-10 08:08:00', 'system', datetime('now'), 'INITIAL' );
INSERT INTO `invoices` (`_id` ,	`invoice_no`  ,	`customer_id`,`posting_date` ,`sales_person_id`,`due_date` ,`total` ,`total_left` ,	`due_date_days_left` ,`created_date` ,`created_by` ,`updated_date`,	`updated_by`) VALUES
	(2 , '2011-12-12_NO_4', 13 , '2011-11-12 14:00:00', 1 , '2011-12-12', 22 , 22 , 2 , '2011-11-12 14:00:00', 'system', datetime('now'), 'INITIAL');
INSERT INTO `invoices` (`_id` ,	`invoice_no`  ,	`customer_id`,`posting_date` ,`sales_person_id`,`due_date` ,`total` ,`total_left` ,	`due_date_days_left` ,`created_date` ,`created_by` ,`updated_date`,	`updated_by`) VALUES
	(3 , '2011-12-12_NO_1', 13 , '2011-11-12 16:00:00', 1 , '2011-12-12', 411 , 411 , 2 , '2011-11-12 16:00:00', 'system', datetime('now'), 'INITIAL');
INSERT INTO `invoices` (`_id` ,	`invoice_no`  ,	`customer_id`,`posting_date` ,`sales_person_id`,`due_date` ,`total` ,`total_left` ,	`due_date_days_left` ,`created_date` ,`created_by` ,`updated_date`,	`updated_by`) VALUES
	(4 , '2012-12-12_NO_6', 12 , '2012-11-10 05:08:00', 1 , '2012-12-12', 1222 , 100 , 5 , '2012-11-10 05:08:00', 'system', datetime('now'), 'INITIAL' );
INSERT INTO `invoices` (`_id` ,	`invoice_no`  ,	`customer_id`,`posting_date` ,`sales_person_id`,`due_date` ,`total` ,`total_left` ,	`due_date_days_left` ,`created_date` ,`created_by` ,`updated_date`,	`updated_by`) VALUES
	(5 , '2011-12-12_NO_5', 13 , '2011-11-12 15:00:00', 1 , '2011-12-12', 22 , 22 , 2 , '2011-11-12 15:00:00', 'system', datetime('now'), 'INITIAL');
INSERT INTO `invoices` (`_id` ,	`invoice_no`  ,	`customer_id`,`posting_date` ,`sales_person_id`,`due_date` ,`total` ,`total_left` ,	`due_date_days_left` ,`created_date` ,`created_by` ,`updated_date`,	`updated_by`) VALUES
	(6 , '2011-12-12_NO_3', 13 , '2011-11-16 16:00:00', 1 , '2011-12-12', 411 , 411 , 2 , '2011-11-16 16:00:00', 'system', datetime('now'), 'INITIAL');

-- customers
CREATE TABLE `customers` (
	`_id` INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , 
	`customer_no` INTEGER, 
	`name` TEXT, 
	`name2` TEXT, 
	`address_id` INTEGER, 
	`city` TEXT, 
	`post_code` TEXT, 
	`phone` TEXT, 
	`mobile` TEXT, 
	`email` TEXT, 
	`company_id` INTEGER, 
	`primary_contact_id` INTEGER, 
	`vat_reg_no` TEXT, 
	`credit_limit_lcy` REAL, 
	`balance_lcy` REAL, 
	`balance_due_lcy` REAL, 
	`payment_terms_code` INTEGER, 
	`priority` INTEGER, 
	`global_dimension` INTEGER, 
	`channel_oran` TEXT,
	`blocked_status` TEXT
);
INSERT INTO  `customers` (`_id`,`customer_no` , `name` , `name2` , 	`address_id` , 	`city` , `post_code` , 	`phone` , `mobile` , `email` , `company_id` , `primary_contact_id` , `vat_reg_no` , `credit_limit_lcy` , `balance_lcy` , `balance_due_lcy` , `payment_terms_code` , `priority` , `global_dimension` , `channel_oran`,`blocked_status` ) VALUES 
	(1, '2011-12-12_NO_1', 'Selecetion product', 'Selection', 4, 'Belgrade', '11000', '+3284545454',  '+3284545454', 'snoop@snoop.com', 121, 21, 'vat', 2122.00, 2211.00, 2121, 21, 2, 21, 'main chanel', '1');
INSERT INTO  `customers` (`_id`,`customer_no` , `name` , `name2` , 	`address_id` , 	`city` , `post_code` , 	`phone` , `mobile` , `email` , `company_id` , `primary_contact_id` , `vat_reg_no` , `credit_limit_lcy` , `balance_lcy` , `balance_due_lcy` , `payment_terms_code` , `priority` , `global_dimension` , `channel_oran`,`blocked_status` ) VALUES 
	(2, '2011-12-12_NO_1', 'Boys still', 'Selection', 4, 'Belgrade', '11000', '+3284545454',  '+3284545454', 'snoop@snoop.com', 121, 21, 'vat', 2122.00, 2211.00, 2121, 21, 2, 21, 'main chanel', '2');
INSERT INTO  `customers` (`_id`,`customer_no` , `name` , `name2` , 	`address_id` , 	`city` , `post_code` , 	`phone` , `mobile` , `email` , `company_id` , `primary_contact_id` , `vat_reg_no` , `credit_limit_lcy` , `balance_lcy` , `balance_due_lcy` , `payment_terms_code` , `priority` , `global_dimension` , `channel_oran`,`blocked_status` ) VALUES 
	(3, '2011-12-12_NO_1', 'Seltor', 'Selection', 4, 'Belgrade', '11000', '+3284545454',  '+3284545454', 'snoop@snoop.com', 121, 21, 'vat', 2122.00, 2211.00, 2121, 21, 2, 21, 'main chanel', '2');
INSERT INTO  `customers` (`_id`,`customer_no` , `name` , `name2` , 	`address_id` , 	`city` , `post_code` , 	`phone` , `mobile` , `email` , `company_id` , `primary_contact_id` , `vat_reg_no` , `credit_limit_lcy` , `balance_lcy` , `balance_due_lcy` , `payment_terms_code` , `priority` , `global_dimension` , `channel_oran`,`blocked_status` ) VALUES 
	(4, '2013-12-12_NO_1', 'Boby soon', 'Selection', 4, 'Belgrade', '11000', '+3284545454',  '+3284545454', 'snoop@snoop.com', 121, 21, 'vat', 2122.00, 2211.00, 2121, 21, 2, 21, 'main chanel', '1');
INSERT INTO  `customers` (`_id`,`customer_no` , `name` , `name2` , 	`address_id` , 	`city` , `post_code` , 	`phone` , `mobile` , `email` , `company_id` , `primary_contact_id` , `vat_reg_no` , `credit_limit_lcy` , `balance_lcy` , `balance_due_lcy` , `payment_terms_code` , `priority` , `global_dimension` , `channel_oran`,`blocked_status` ) VALUES 
	(5, '2013-12-12_NO_1', 'Senegal industry', 'Selection', 4, 'Belgrade', '11000', '+3284545454',  '+3284545454', 'snoop@snoop.com', 121, 21, 'vat', 2122.00, 2211.00, 2121, 21, 2, 21, 'main chanel', '1');
INSERT INTO  `customers` (`_id`,`customer_no` , `name` , `name2` , 	`address_id` , 	`city` , `post_code` , 	`phone` , `mobile` , `email` , `company_id` , `primary_contact_id` , `vat_reg_no` , `credit_limit_lcy` , `balance_lcy` , `balance_due_lcy` , `payment_terms_code` , `priority` , `global_dimension` , `channel_oran`,`blocked_status` ) VALUES 
	(6, '2012-12-12_NO_1', 'Activate company', 'Selection', 4, 'Belgrade', '11000', '+3284545454',  '+3284545454', 'snoop@snoop.com', 121, 21, 'vat', 2122.00, 2211.00, 2121, 21, 2, 21, 'main chanel', '0');
INSERT INTO  `customers` (`_id`,`customer_no` , `name` , `name2` , 	`address_id` , 	`city` , `post_code` , 	`phone` , `mobile` , `email` , `company_id` , `primary_contact_id` , `vat_reg_no` , `credit_limit_lcy` , `balance_lcy` , `balance_due_lcy` , `payment_terms_code` , `priority` , `global_dimension` , `channel_oran`,`blocked_status` ) VALUES 
	(7, '2112-12-12_NO_1', 'S&S style', 'Selection', 4, 'Belgrade', '11000', '+3284545454',  '+3284545454', 'snoop@snoop.com', 121, 21, 'vat', 2122.00, 2211.00, 2121, 21, 2, 21, 'main chanel', '0');
INSERT INTO  `customers` (`_id`,`customer_no` , `name` , `name2` , 	`address_id` , 	`city` , `post_code` , 	`phone` , `mobile` , `email` , `company_id` , `primary_contact_id` , `vat_reg_no` , `credit_limit_lcy` , `balance_lcy` , `balance_due_lcy` , `payment_terms_code` , `priority` , `global_dimension` , `channel_oran`,`blocked_status` ) VALUES 
	(8, '1999-12-12_NO_1', 'Alpha', 'Selection', 4, 'Belgrade', '11000', '+3284545454',  '+3284545454', 'snoop@snoop.com', 121, 21, 'vat', 2122.00, 2211.00, 2121, 21, 2, 21, 'main chanel', '0');

-- contacts
CREATE TABLE `contacts` (
	`_id` INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL,
	`contact_no` TEXT,
	`contact_type` TEXT,
	`name` TEXT,
	`name2` TEXT,
	`address` TEXT,
	`city` TEXT,
	`post_code` TEXT,
	`phone` TEXT,
	`mobile_phone` TEXT,
	`email` TEXT,
	`company_no` TEXT,
	`company_id` TEXT,
	`vat_registration` TEXT,
	`sales_person_id` TEXT,
	`job_title` TEXT
	`created_date` TEXT,
	`created_by` TEXT,
	`updated_date` TEXT,
	`updated_by`TEXT
);
CREATE TRIGGER IF NOT EXISTS "log_new_contact" AFTER INSERT ON "contacts" 
BEGIN 
	update contacts set 
	created_date = datetime('now'),
	created_by = (select username from users where active = 1)
	where _id = new._id; 
END;
CREATE TRIGGER IF NOT EXISTS "log_contact_change" AFTER UPDATE ON "contacts" 
BEGIN 
	update contacts set 
	updated_date = datetime('now'),
	updated_by = (select username from users where active = 1)
	where _id = new._id; 
END;

-- items
CREATE TABLE `items` (
	`_id` INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL ,
	`item_no` TEXT,
	`description` TEXT,
	`description2` TEXT,
	`unit_of_measure` TEXT,
	`category_code` TEXT,
	`group_code` TEXT,
	`campaign_status` INTEGER,
	`connected_spec_ship_item` TEXT,
	`unit_sales_price_eur` REAL,
	`unit_sales_price_din` REAL,
	`campaign_code` TEXT,
	`cmpaign_start_date` TEXT,
	`campaign_end_date` TEXT,
	`created_date` TEXT,
	`created_by` TEXT,
	`updated_date` TEXT,
	`updated_by` TEXT
);
CREATE TRIGGER IF NOT EXISTS "log_new_item" AFTER INSERT ON "items" 
BEGIN 
	update items set 
	created_date = datetime('now'),
	created_by = (select username from users where active = 1)
	where _id = new._id; 
END;
CREATE TRIGGER IF NOT EXISTS "log_item_change" AFTER UPDATE ON "items" 
BEGIN 
	update items set 
	updated_date = datetime('now'),
	updated_by = (select username from users where active = 1)
	where _id = new._id; 
END;
INSERT INTO `items` (`_id` ,`item_no` , `description` ,`description2` ,`unit_of_measure` ,`category_code` ,`group_code` ,`campaign_status` ,`connected_spec_ship_item` ,`unit_sales_price_eur` ,`unit_sales_price_din` ,`campaign_code` ,`cmpaign_start_date` ,`campaign_end_date` ,`created_date` ,`created_by` ,`updated_date` ,`updated_by` ) VALUES 
	(1, '1012-12-12-NO1','simple product', 'best article', 'quantity', 'G1', '0', 0, 'spec ship item', 1, 1, 'code', '2012-12-12 20:20:20', '2012-12-12 20:20:20', datetime('now'), 'INITIAL',datetime('now'), 'INITIAL' );
INSERT INTO `items` (`_id` ,`item_no` , `description` ,`description2` ,`unit_of_measure` ,`category_code` ,`group_code` ,`campaign_status` ,`connected_spec_ship_item` ,`unit_sales_price_eur` ,`unit_sales_price_din` ,`campaign_code` ,`cmpaign_start_date` ,`campaign_end_date` ,`created_date` ,`created_by` ,`updated_date` ,`updated_by` ) VALUES 
	(2, '2001-12-12-NO1','complex panorama', 'best article', 'quantity', 'G1', '0', 0, 'spec ship item', 1, 1, 'code', '2012-12-12 20:20:20', '2012-12-12 20:20:20', datetime('now'), 'INITIAL',datetime('now'), 'INITIAL' );
INSERT INTO `items` (`_id` ,`item_no` , `description` ,`description2` ,`unit_of_measure` ,`category_code` ,`group_code` ,`campaign_status` ,`connected_spec_ship_item` ,`unit_sales_price_eur` ,`unit_sales_price_din` ,`campaign_code` ,`cmpaign_start_date` ,`campaign_end_date` ,`created_date` ,`created_by` ,`updated_date` ,`updated_by` ) VALUES 
	(3, '2011-12-12-NO1','best ice', 'best article', 'quantity', 'G1', '0', 0, 'spec ship item', 1, 1, 'code', '2012-12-12 20:20:20', '2012-12-12 20:20:20', datetime('now'), 'INITIAL',datetime('now'), 'INITIAL' );
INSERT INTO `items` (`_id` ,`item_no` , `description` ,`description2` ,`unit_of_measure` ,`category_code` ,`group_code` ,`campaign_status` ,`connected_spec_ship_item` ,`unit_sales_price_eur` ,`unit_sales_price_din` ,`campaign_code` ,`cmpaign_start_date` ,`campaign_end_date` ,`created_date` ,`created_by` ,`updated_date` ,`updated_by` ) VALUES 
	(4, '1012-12-12-NO1','Worst gum', 'best article', 'quantity', 'G1', '1', 1, 'spec ship item', 1, 1, 'code', '2012-12-12 20:20:20', '2012-12-12 20:20:20', datetime('now'), 'INITIAL',datetime('now'), 'INITIAL' );
INSERT INTO `items` (`_id` ,`item_no` , `description` ,`description2` ,`unit_of_measure` ,`category_code` ,`group_code` ,`campaign_status` ,`connected_spec_ship_item` ,`unit_sales_price_eur` ,`unit_sales_price_din` ,`campaign_code` ,`cmpaign_start_date` ,`campaign_end_date` ,`created_date` ,`created_by` ,`updated_date` ,`updated_by` ) VALUES 
	(5, '1999-12-12-NO1','Useful watch', 'best article', 'quantity', 'G1', '1', 1, 'spec ship item', 1, 1, 'code', '2012-12-12 20:20:20', '2012-12-12 20:20:20', datetime('now'), 'INITIAL',datetime('now'), 'INITIAL' );
INSERT INTO `items` (`_id` ,`item_no` , `description` ,`description2` ,`unit_of_measure` ,`category_code` ,`group_code` ,`campaign_status` ,`connected_spec_ship_item` ,`unit_sales_price_eur` ,`unit_sales_price_din` ,`campaign_code` ,`cmpaign_start_date` ,`campaign_end_date` ,`created_date` ,`created_by` ,`updated_date` ,`updated_by` ) VALUES 
	(6, '2012-12-12-NO1','Temp folder', 'best article', 'quantity', 'G1', '2',2, 'spec ship item', 1, 1, 'code', '2012-12-12 20:20:20', '2012-12-12 20:20:20', datetime('now'), 'INITIAL',datetime('now'), 'INITIAL' );
INSERT INTO `items` (`_id` ,`item_no` , `description` ,`description2` ,`unit_of_measure` ,`category_code` ,`group_code` ,`campaign_status` ,`connected_spec_ship_item` ,`unit_sales_price_eur` ,`unit_sales_price_din` ,`campaign_code` ,`cmpaign_start_date` ,`campaign_end_date` ,`created_date` ,`created_by` ,`updated_date` ,`updated_by` ) VALUES 
	(7, '2012-12-12-NO1','Lion snoope', 'best article', 'quantity', 'G1', '2', 2, 'spec ship item', 1, 1, 'code', '2012-12-12 20:20:20', '2012-12-12 20:20:20', datetime('now'), 'INITIAL',datetime('now'), 'INITIAL' );
INSERT INTO `items` (`_id` ,`item_no` , `description` ,`description2` ,`unit_of_measure` ,`category_code` ,`group_code` ,`campaign_status` ,`connected_spec_ship_item` ,`unit_sales_price_eur` ,`unit_sales_price_din` ,`campaign_code` ,`cmpaign_start_date` ,`campaign_end_date` ,`created_date` ,`created_by` ,`updated_date` ,`updated_by` ) VALUES 
	(8, '2012-12-12-NO1','Article', 'best article', 'quantity', 'G1', '2', 2, 'spec ship item', 1, 1, 'code', '2012-12-12 20:20:20', '2012-12-12 20:20:20', datetime('now'), 'INITIAL',datetime('now'), 'INITIAL' );

-- visits
CREATE TABLE `visits` (
	`_id` INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL ,
	`sales_person_id` INTEGER,
	`visit_date` TEXT,
	`customer_id` INTEGER,
	`line_no` INTEGER,
	`entry_type` TEXT,
	`odometer` INTEGER,
	`departure_time` TEXT,
	`arrival_time` TEXT,
	`visit_result` TEXT,
	`note` TEXT,
	`created_date` TEXT,
	`created_by` TEXT,
	`updated_date` TEXT,
	`updated_by` TEXT
);
CREATE TRIGGER IF NOT EXISTS "log_new_visit" AFTER INSERT ON "visits" 
BEGIN 
	update visits set 
	created_date = datetime('now'),
	created_by = (select username from users where active = 1)
	where _id = new._id; 
END;
CREATE TRIGGER IF NOT EXISTS "log_visit_change" AFTER UPDATE ON "visits" 
BEGIN 
	update visits set 
	updated_date = datetime('now'),
	updated_by = (select username from users where active = 1)
	where _id = new._id; 
END;
INSERT INTO `visits` VALUES ('1','1','2012-12-18 00:00:00','1',null,null,'1','2012-12-18 00:00:00','2012-12-18 00:00:00',null,'dada','2012-12-18 01:11:12','vlada','2012-12-18 01:11:12','vlada');
INSERT INTO `visits` VALUES ('2','1','2012-12-18 00:00:00','2',null,null,'1000','2012-12-18 00:00:00','2012-12-18 00:00:00',null,'dada','2012-12-18 01:11:12','vlada','2012-12-18 01:11:12','vlada');
CREATE TABLE `sale_orders` (
	`_id` INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL,
	`sales_order_no` TEXT,
	`document_type` INTEGER,
	`customer_id` INTEGER,
	`order_date` TEXT,
	`location_code` TEXT,
	`shortcut_dimension_1_code` TEXT,
	`currency_code` TEXT,
	`external_document_no` TEXT,
	`quote_no` TEXT,
	`backorder_shipment_status` INTEGER,
	`order_status_for_shipment` INTEGER,
	`fin_control_status` INTEGER,
	`order_condition_status` INTEGER,
	`used_credit_limit_by_employee` TEXT,
	`order_value_status` INTEGER,
	`quote_realized_status` INTEGER,
	`special_quote` INTEGER,
	`quote_valid_date_to` TEXT,
	`cust_uses_transit_cust` TEXT,
	`sales_person_id` INTEGER,
	`customer_address_id` INTEGER,
	`contact_phone` TEXT,
	`payment_option` TEXT,
	`check_status_phone` TEXT,
	`total` REAL,
	`total_discount` REAL,
	`total_pdv` REAL,
	`total_items` REAL,
	`hide_rebate` INTEGER DEFAULT 0,
	`further_sale` INTEGER DEFAULT 0,
	`note1` TEXT,
	`note2` TEXT,
	`note3` TEXT,
	`created_date` TEXT,
	`created_by` TEXT,
	`updated_date` TEXT,
	`updated_by` TEXT
);

INSERT INTO `sale_orders` (	`_id` ,	`sales_order_no` ,	`document_type` ,	`customer_id` ,	`order_date` ,	`location_code` ,	`shortcut_dimension_1_code` ,	`currency_code` ,	`external_document_no` ,	`quote_no` ,	`backorder_shipment_status` ,	`order_status_for_shipment` ,	`fin_control_status` ,`order_condition_status` ,	`used_credit_limit_by_employee` ,	`order_value_status` ,	`quote_realized_status` ,	`special_quote` ,	`quote_valid_date_to` ,	`cust_uses_transit_cust` ,	`sales_person_id` ,	`customer_address_id`,	`contact_phone` ,	`payment_option` ,	`check_status_phone` ,	`total` ,	`total_discount` ,	`total_pdv` ,	`total_items` ,	`hide_rebate` ,	`further_sale`,	`note1` ,	`note2` ,	`note3` ,	`created_date` ,	`created_by` ,	`updated_date` ,	`updated_by`) VALUES
(1, 'SALE_ORDER_NO1', 0,2, '2012-06-12 20:20:20', 'location code', 'shorecut', 'cuurent_code', 'ext doc', 'quote no', 2,2,2,2,'used limitation', 2,2,2, '2012-12-12 20:20:20', 'uses transit', 1, 2,'06457575','payment op', 'startus phone', 2000, 1000, 5, 200, 0,0, 'note1', 'note2', 'note3', '2012-12-12 20:20:20','sys', '2012-12-12 20:20:20', 'sys');
INSERT INTO `sale_orders` (	`_id` ,	`sales_order_no` ,	`document_type` ,	`customer_id` ,	`order_date` ,	`location_code` ,	`shortcut_dimension_1_code` ,	`currency_code` ,	`external_document_no` ,	`quote_no` ,	`backorder_shipment_status` ,	`order_status_for_shipment` ,	`fin_control_status` ,`order_condition_status` ,	`used_credit_limit_by_employee` ,	`order_value_status` ,	`quote_realized_status` ,	`special_quote` ,	`quote_valid_date_to` ,	`cust_uses_transit_cust` ,	`sales_person_id` ,	`customer_address_id`,	`contact_phone` ,	`payment_option` ,	`check_status_phone` ,	`total` ,	`total_discount` ,	`total_pdv` ,	`total_items` ,	`hide_rebate` ,	`further_sale`,	`note1` ,	`note2` ,	`note3` ,	`created_date` ,	`created_by` ,	`updated_date` ,	`updated_by`) VALUES
(2, 'SALE_ORDER_NO2', 0,2, '2012-12-12 10:20:20', 'location code', 'shorecut', 'cuurent_code', 'ext doc', 'quote no', 2,2,2,2,'used limitation', 2,2,2, '2012-12-12 20:20:20', 'uses transit', 1, 2,'06457575','payment op', 'startus phone', 2500, 1000, 5, 200, 0,0, 'note1', 'note2', 'note3', '2012-12-12 20:20:20','sys', '2012-12-12 20:20:20', 'sys');
INSERT INTO `sale_orders` (	`_id` ,	`sales_order_no` ,	`document_type` ,	`customer_id` ,	`order_date` ,	`location_code` ,	`shortcut_dimension_1_code` ,	`currency_code` ,	`external_document_no` ,	`quote_no` ,	`backorder_shipment_status` ,	`order_status_for_shipment` ,	`fin_control_status` ,`order_condition_status` ,	`used_credit_limit_by_employee` ,	`order_value_status` ,	`quote_realized_status` ,	`special_quote` ,	`quote_valid_date_to` ,	`cust_uses_transit_cust` ,	`sales_person_id` ,	`customer_address_id`,	`contact_phone` ,	`payment_option` ,	`check_status_phone` ,	`total` ,	`total_discount` ,	`total_pdv` ,	`total_items` ,	`hide_rebate` ,	`further_sale`,	`note1` ,	`note2` ,	`note3` ,	`created_date` ,	`created_by` ,	`updated_date` ,	`updated_by`) VALUES
(3, 'SPEC_SUPPLY_NO3', 1,2, '2010-10-12 10:20:20', 'location code', 'shorecut', 'cuurent_code', 'ext doc', 'quote no', 2,2,2,2,'used limitation', 2,2,2, '2012-12-12 20:20:20', 'uses transit', 1, 2,'06457575','payment op', 'startus phone', 1000, 1000, 5, 200, 0,0, 'note1', 'note2', 'note3', '2012-12-12 20:20:20','sys', '2012-12-12 20:20:20', 'sys');
INSERT INTO `sale_orders` (	`_id` ,	`sales_order_no` ,	`document_type` ,	`customer_id` ,	`order_date` ,	`location_code` ,	`shortcut_dimension_1_code` ,	`currency_code` ,	`external_document_no` ,	`quote_no` ,	`backorder_shipment_status` ,	`order_status_for_shipment` ,	`fin_control_status` ,`order_condition_status` ,	`used_credit_limit_by_employee` ,	`order_value_status` ,	`quote_realized_status` ,	`special_quote` ,	`quote_valid_date_to` ,	`cust_uses_transit_cust` ,	`sales_person_id` ,	`customer_address_id`,	`contact_phone` ,	`payment_option` ,	`check_status_phone` ,	`total` ,	`total_discount` ,	`total_pdv` ,	`total_items` ,	`hide_rebate` ,	`further_sale`,	`note1` ,	`note2` ,	`note3` ,	`created_date` ,	`created_by` ,	`updated_date` ,	`updated_by`) VALUES
(4, 'Supply_NO4', 1,1, '2012-12-12 00:20:20', 'location code', 'shorecut', 'cuurent_code', 'ext doc', 'quote no', 2,2,2,2,'used limitation', 2,2,2, '2012-12-12 20:20:20', 'uses transit', 1, 2,'06457575','payment op', 'startus phone', 22000, 1000, 5, 200, 0,0, 'note1', 'note2', 'note3', '2012-12-12 20:20:20','sys', '2012-12-12 20:20:20', 'sys');
INSERT INTO `sale_orders` (	`_id` ,	`sales_order_no` ,	`document_type` ,	`customer_id` ,	`order_date` ,	`location_code` ,	`shortcut_dimension_1_code` ,	`currency_code` ,	`external_document_no` ,	`quote_no` ,	`backorder_shipment_status` ,	`order_status_for_shipment` ,	`fin_control_status` ,`order_condition_status` ,	`used_credit_limit_by_employee` ,	`order_value_status` ,	`quote_realized_status` ,	`special_quote` ,	`quote_valid_date_to` ,	`cust_uses_transit_cust` ,	`sales_person_id` ,	`customer_address_id`,	`contact_phone` ,	`payment_option` ,	`check_status_phone` ,	`total` ,	`total_discount` ,	`total_pdv` ,	`total_items` ,	`hide_rebate` ,	`further_sale`,	`note1` ,	`note2` ,	`note3` ,	`created_date` ,	`created_by` ,	`updated_date` ,	`updated_by`) VALUES
(5, 'ALIEN_SUPPLY_NO5', 1,1, '2010-12-12 08:20:20', 'location code', 'shorecut', 'cuurent_code', 'ext doc', 'quote no', 2,2,2,2,'used limitation', 2,2,2, '2012-12-12 20:20:20', 'uses transit', 1, 2,'06457575','payment op', 'startus phone', 8000, 1000, 5, 200, 0,0, 'note1', 'note2', 'note3', '2012-12-12 20:20:20','sys', '2012-12-12 20:20:20', 'sys');
INSERT INTO `sale_orders` (	`_id` ,	`sales_order_no` ,	`document_type` ,	`customer_id` ,	`order_date` ,	`location_code` ,	`shortcut_dimension_1_code` ,	`currency_code` ,	`external_document_no` ,	`quote_no` ,	`backorder_shipment_status` ,	`order_status_for_shipment` ,	`fin_control_status` ,`order_condition_status` ,	`used_credit_limit_by_employee` ,	`order_value_status` ,	`quote_realized_status` ,	`special_quote` ,	`quote_valid_date_to` ,	`cust_uses_transit_cust` ,	`sales_person_id` ,	`customer_address_id`,	`contact_phone` ,	`payment_option` ,	`check_status_phone` ,	`total` ,	`total_discount` ,	`total_pdv` ,	`total_items` ,	`hide_rebate` ,	`further_sale`,	`note1` ,	`note2` ,	`note3` ,	`created_date` ,	`created_by` ,	`updated_date` ,	`updated_by`) VALUES
(6, 'SALE_ORDER_NO6', 1,1, '2011-12-12 09:20:20', 'location code', 'shorecut', 'cuurent_code', 'ext doc', 'quote no', 2,2,2,2,'used limitation', 2,2,2, '2012-12-12 20:20:20', 'uses transit', 1, 2,'06457575','payment op', 'startus phone', 6000, 1000, 5, 200, 0,0, 'note1', 'note2', 'note3', '2012-12-12 20:20:20','sys', '2012-12-12 20:20:20', 'sys');


CREATE TRIGGER IF NOT EXISTS "log_new_sale_order" AFTER INSERT ON "sale_orders" 
BEGIN 
	update sale_orders set 
	created_date = datetime('now'),
	created_by = (select username from users where active = 1)
	where _id = new._id; 
END;
CREATE TRIGGER IF NOT EXISTS "log_sale_order_change" AFTER UPDATE ON "sale_orders" 
BEGIN 
	update sale_orders set 
	updated_date = datetime('now'),
	updated_by = (select username from users where active = 1)
	where _id = new._id; 
END;
CREATE TABLE `sale_order_lines` (
	`_id` INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL,
	`sale_order_id` INTEGER,
	`line_no` INTEGER,
	`item_id` INTEGER,
	`quantity` REAL,
	`unit_of_measure` TEXT,
	`price` REAL,
	`min_discount` REAL,
	`max_discount` REAL,
	`real_discount` REAL,
	`line_total` REAL,
	`line_origin` TEXT,
	`price_eur` REAL,
	`campaign_status` INTEGER,
	`verify_status` INTEGER,
	`price_discount_status` INTEGER,
	`quantity_available_status` INTEGER,
	`created_date` TEXT,
	`created_by` TEXT,
	`updated_date` TEXT,
	`updated_by` TEXT
);
CREATE TRIGGER IF NOT EXISTS "log_new_sale_order_line" AFTER INSERT ON "sale_order_lines" 
BEGIN 
	update sale_order_lines set 
	created_date = datetime('now'),
	created_by = (select username from users where active = 1)
	where _id = new._id; 
END;
CREATE TRIGGER IF NOT EXISTS "log_sale_order_line_change" AFTER UPDATE ON "sale_order_lines" 
BEGIN 
	update sale_order_lines set 
	updated_date = datetime('now'),
	updated_by = (select username from users where active = 1)
	where _id = new._id; 
END;
