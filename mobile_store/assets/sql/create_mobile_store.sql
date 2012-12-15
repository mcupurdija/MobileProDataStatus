CREATE TABLE `USERS` ( 
	`_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
	`username` TEXT,
	`pass` TEXT,
	`sales_person_id` INTEGER,
	`last_login` TEXT
);
INSERT INTO `USERS` (username,pass,sales_person_id, last_login) VALUES 
	('tica', 'tica', 1, 'yesterday'); 

CREATE TABLE `invoices` (
	`_id` INTEGER PRIMARY KEY  NOT NULL ,
	`no` TEXT NOT NULL  DEFAULT (0) ,
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
INSERT INTO `invoices` (`_id` ,	`no`  ,	`customer_id`,`posting_date` ,`sales_person_id`,`due_date` ,`total` ,`total_left` ,	`due_date_days_left` ,`created_date` ,`created_by` ,`updated_date`,	`updated_by`) VALUES
	(1 , '2012-12-12_NO_1', 12 , '2012-11-10 08:08:00.000', 1 , '2012-12-12', 1222 , 100 , 5 , '2012-11-10 08:08:00.000', 'system', '2012-11-11', 'sytem' );
INSERT INTO `invoices` (`_id` ,	`no`  ,	`customer_id`,`posting_date` ,`sales_person_id`,`due_date` ,`total` ,`total_left` ,	`due_date_days_left` ,`created_date` ,`created_by` ,`updated_date`,	`updated_by`) VALUES
	(2 , '2011-12-12_NO_4', 13 , '2011-11-12 14:00:00.000', 1 , '2011-12-12', 22 , 22 , 2 , '2011-11-12 14:00:00.000', 'system', '2011-11-11', 'sytem');
INSERT INTO `invoices` (`_id` ,	`no`  ,	`customer_id`,`posting_date` ,`sales_person_id`,`due_date` ,`total` ,`total_left` ,	`due_date_days_left` ,`created_date` ,`created_by` ,`updated_date`,	`updated_by`) VALUES
	(3 , '2011-12-12_NO_1', 13 , '2011-11-12 16:00:00.000', 1 , '2011-12-12', 411 , 411 , 2 , '2011-11-12 16:00:00.000', 'system', '2011-11-11', 'sytem');
INSERT INTO `invoices` (`_id` ,	`no`  ,	`customer_id`,`posting_date` ,`sales_person_id`,`due_date` ,`total` ,`total_left` ,	`due_date_days_left` ,`created_date` ,`created_by` ,`updated_date`,	`updated_by`) VALUES
	(4 , '2012-12-12_NO_6', 12 , '2012-11-10 05:08:00.000', 1 , '2012-12-12', 1222 , 100 , 5 , '2012-11-10 05:08:00.000', 'system', '2012-11-11', 'sytem' );
INSERT INTO `invoices` (`_id` ,	`no`  ,	`customer_id`,`posting_date` ,`sales_person_id`,`due_date` ,`total` ,`total_left` ,	`due_date_days_left` ,`created_date` ,`created_by` ,`updated_date`,	`updated_by`) VALUES
	(5 , '2011-12-12_NO_5', 13 , '2011-11-12 15:00:00.000', 1 , '2011-12-12', 22 , 22 , 2 , '2011-11-12 15:00:00.000', 'system', '2011-11-11', 'sytem');
INSERT INTO `invoices` (`_id` ,	`no`  ,	`customer_id`,`posting_date` ,`sales_person_id`,`due_date` ,`total` ,`total_left` ,	`due_date_days_left` ,`created_date` ,`created_by` ,`updated_date`,	`updated_by`) VALUES
	(6 , '2011-12-12_NO_3', 13 ,'2011-11-16 16:00:00.000', 1 , '2011-12-12', 411 , 411 , 2 , '2011-11-16 16:00:00.000', 'system', '2011-11-11', 'sytem');
	
CREATE TABLE `customers` (
	`_id` INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , 
	`no` INTEGER, 
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
			
			
INSERT INTO  `customers` (`_id`,`no` , `name` , `name2` , 	`address_id` , 	`city` , `post_code` , 	`phone` , `mobile` , `email` , `company_id` , `primary_contact_id` , `vat_reg_no` , `credit_limit_lcy` , `balance_lcy` , `balance_due_lcy` , `payment_terms_code` , `priority` , `global_dimension` , `channel_oran`,`blocked_status` ) VALUES 
			(1, '2011-12-12_NO_1', 'Selecetion product', 'Selection', 4, 'Belgrade', '11000', '+3284545454',  '+3284545454', 'snoop@snoop.com', 121, 21, 'vat', 2122.00, 2211.00, 2121, 21, 2, 21, 'main chanel', '1');
INSERT INTO  `customers` (`_id`,`no` , `name` , `name2` , 	`address_id` , 	`city` , `post_code` , 	`phone` , `mobile` , `email` , `company_id` , `primary_contact_id` , `vat_reg_no` , `credit_limit_lcy` , `balance_lcy` , `balance_due_lcy` , `payment_terms_code` , `priority` , `global_dimension` , `channel_oran`,`blocked_status` ) VALUES 
			(2, '2011-12-12_NO_1', 'Boys still', 'Selection', 4, 'Belgrade', '11000', '+3284545454',  '+3284545454', 'snoop@snoop.com', 121, 21, 'vat', 2122.00, 2211.00, 2121, 21, 2, 21, 'main chanel', '2');
INSERT INTO  `customers` (`_id`,`no` , `name` , `name2` , 	`address_id` , 	`city` , `post_code` , 	`phone` , `mobile` , `email` , `company_id` , `primary_contact_id` , `vat_reg_no` , `credit_limit_lcy` , `balance_lcy` , `balance_due_lcy` , `payment_terms_code` , `priority` , `global_dimension` , `channel_oran`,`blocked_status` ) VALUES 
			(3, '2011-12-12_NO_1', 'Seltor', 'Selection', 4, 'Belgrade', '11000', '+3284545454',  '+3284545454', 'snoop@snoop.com', 121, 21, 'vat', 2122.00, 2211.00, 2121, 21, 2, 21, 'main chanel', '2');
INSERT INTO  `customers` (`_id`,`no` , `name` , `name2` , 	`address_id` , 	`city` , `post_code` , 	`phone` , `mobile` , `email` , `company_id` , `primary_contact_id` , `vat_reg_no` , `credit_limit_lcy` , `balance_lcy` , `balance_due_lcy` , `payment_terms_code` , `priority` , `global_dimension` , `channel_oran`,`blocked_status` ) VALUES 
			(4, '2013-12-12_NO_1', 'Boby soon', 'Selection', 4, 'Belgrade', '11000', '+3284545454',  '+3284545454', 'snoop@snoop.com', 121, 21, 'vat', 2122.00, 2211.00, 2121, 21, 2, 21, 'main chanel', '1');
INSERT INTO  `customers` (`_id`,`no` , `name` , `name2` , 	`address_id` , 	`city` , `post_code` , 	`phone` , `mobile` , `email` , `company_id` , `primary_contact_id` , `vat_reg_no` , `credit_limit_lcy` , `balance_lcy` , `balance_due_lcy` , `payment_terms_code` , `priority` , `global_dimension` , `channel_oran`,`blocked_status` ) VALUES 
			(5, '2013-12-12_NO_1', 'Senegal industry', 'Selection', 4, 'Belgrade', '11000', '+3284545454',  '+3284545454', 'snoop@snoop.com', 121, 21, 'vat', 2122.00, 2211.00, 2121, 21, 2, 21, 'main chanel', '1');
INSERT INTO  `customers` (`_id`,`no` , `name` , `name2` , 	`address_id` , 	`city` , `post_code` , 	`phone` , `mobile` , `email` , `company_id` , `primary_contact_id` , `vat_reg_no` , `credit_limit_lcy` , `balance_lcy` , `balance_due_lcy` , `payment_terms_code` , `priority` , `global_dimension` , `channel_oran`,`blocked_status` ) VALUES 
			(6, '2012-12-12_NO_1', 'Activate company', 'Selection', 4, 'Belgrade', '11000', '+3284545454',  '+3284545454', 'snoop@snoop.com', 121, 21, 'vat', 2122.00, 2211.00, 2121, 21, 2, 21, 'main chanel', '0');
INSERT INTO  `customers` (`_id`,`no` , `name` , `name2` , 	`address_id` , 	`city` , `post_code` , 	`phone` , `mobile` , `email` , `company_id` , `primary_contact_id` , `vat_reg_no` , `credit_limit_lcy` , `balance_lcy` , `balance_due_lcy` , `payment_terms_code` , `priority` , `global_dimension` , `channel_oran`,`blocked_status` ) VALUES 
			(7, '2112-12-12_NO_1', 'S&S style', 'Selection', 4, 'Belgrade', '11000', '+3284545454',  '+3284545454', 'snoop@snoop.com', 121, 21, 'vat', 2122.00, 2211.00, 2121, 21, 2, 21, 'main chanel', '0');
INSERT INTO  `customers` (`_id`,`no` , `name` , `name2` , 	`address_id` , 	`city` , `post_code` , 	`phone` , `mobile` , `email` , `company_id` , `primary_contact_id` , `vat_reg_no` , `credit_limit_lcy` , `balance_lcy` , `balance_due_lcy` , `payment_terms_code` , `priority` , `global_dimension` , `channel_oran`,`blocked_status` ) VALUES 
			(8, '1999-12-12_NO_1', 'Alpha', 'Selection', 4, 'Belgrade', '11000', '+3284545454',  '+3284545454', 'snoop@snoop.com', 121, 21, 'vat', 2122.00, 2211.00, 2121, 21, 2, 21, 'main chanel', '0');
			
CREATE TABLE `items` (
			`_id` INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL ,
			`no` TEXT,
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
			`updated_by` TEXT);
			
INSERT INTO `items` (`_id` ,`no` , `description` ,`description2` ,`unit_of_measure` ,`category_code` ,`group_code` ,`campaign_status` ,`connected_spec_ship_item` ,`unit_sales_price_eur` ,`unit_sales_price_din` ,`campaign_code` ,`cmpaign_start_date` ,`campaign_end_date` ,`created_date` ,`created_by` ,`updated_date` ,`updated_by` ) VALUES 
					(1, '1012-12-12-NO1','simple product', 'best article', 'quantity', 'G1', '0', 0, 'spec ship item', 1, 1, 'code', '2012-12-12 20:20:20.000', '2012-12-12 20:20:20.000', '2012-12-12 20:20:20.000', 'system','2012-12-12 20:20:20.000', 'system' );
INSERT INTO `items` (`_id` ,`no` , `description` ,`description2` ,`unit_of_measure` ,`category_code` ,`group_code` ,`campaign_status` ,`connected_spec_ship_item` ,`unit_sales_price_eur` ,`unit_sales_price_din` ,`campaign_code` ,`cmpaign_start_date` ,`campaign_end_date` ,`created_date` ,`created_by` ,`updated_date` ,`updated_by` ) VALUES 
					(2, '2001-12-12-NO1','complex panorama', 'best article', 'quantity', 'G1', '0', 0, 'spec ship item', 1, 1, 'code', '2012-12-12 20:20:20.000', '2012-12-12 20:20:20.000', '2012-12-12 20:20:20.000', 'system','2012-12-12 20:20:20.000', 'system' );
INSERT INTO `items` (`_id` ,`no` , `description` ,`description2` ,`unit_of_measure` ,`category_code` ,`group_code` ,`campaign_status` ,`connected_spec_ship_item` ,`unit_sales_price_eur` ,`unit_sales_price_din` ,`campaign_code` ,`cmpaign_start_date` ,`campaign_end_date` ,`created_date` ,`created_by` ,`updated_date` ,`updated_by` ) VALUES 
					(3, '2011-12-12-NO1','best ice', 'best article', 'quantity', 'G1', '0', 0, 'spec ship item', 1, 1, 'code', '2012-12-12 20:20:20.000', '2012-12-12 20:20:20.000', '2012-12-12 20:20:20.000', 'system','2012-12-12 20:20:20.000', 'system' );
INSERT INTO `items` (`_id` ,`no` , `description` ,`description2` ,`unit_of_measure` ,`category_code` ,`group_code` ,`campaign_status` ,`connected_spec_ship_item` ,`unit_sales_price_eur` ,`unit_sales_price_din` ,`campaign_code` ,`cmpaign_start_date` ,`campaign_end_date` ,`created_date` ,`created_by` ,`updated_date` ,`updated_by` ) VALUES 
					(4, '1012-12-12-NO1','Worst gum', 'best article', 'quantity', 'G1', '1', 1, 'spec ship item', 1, 1, 'code', '2012-12-12 20:20:20.000', '2012-12-12 20:20:20.000', '2012-12-12 20:20:20.000', 'system','2012-12-12 20:20:20.000', 'system' );
INSERT INTO `items` (`_id` ,`no` , `description` ,`description2` ,`unit_of_measure` ,`category_code` ,`group_code` ,`campaign_status` ,`connected_spec_ship_item` ,`unit_sales_price_eur` ,`unit_sales_price_din` ,`campaign_code` ,`cmpaign_start_date` ,`campaign_end_date` ,`created_date` ,`created_by` ,`updated_date` ,`updated_by` ) VALUES 
					(5, '1999-12-12-NO1','Useful watch', 'best article', 'quantity', 'G1', '1', 1, 'spec ship item', 1, 1, 'code', '2012-12-12 20:20:20.000', '2012-12-12 20:20:20.000', '2012-12-12 20:20:20.000', 'system','2012-12-12 20:20:20.000', 'system' );
INSERT INTO `items` (`_id` ,`no` , `description` ,`description2` ,`unit_of_measure` ,`category_code` ,`group_code` ,`campaign_status` ,`connected_spec_ship_item` ,`unit_sales_price_eur` ,`unit_sales_price_din` ,`campaign_code` ,`cmpaign_start_date` ,`campaign_end_date` ,`created_date` ,`created_by` ,`updated_date` ,`updated_by` ) VALUES 
					(6, '2012-12-12-NO1','Temp folder', 'best article', 'quantity', 'G1', '2',2, 'spec ship item', 1, 1, 'code', '2012-12-12 20:20:20.000', '2012-12-12 20:20:20.000', '2012-12-12 20:20:20.000', 'system','2012-12-12 20:20:20.000', 'system' );
INSERT INTO `items` (`_id` ,`no` , `description` ,`description2` ,`unit_of_measure` ,`category_code` ,`group_code` ,`campaign_status` ,`connected_spec_ship_item` ,`unit_sales_price_eur` ,`unit_sales_price_din` ,`campaign_code` ,`cmpaign_start_date` ,`campaign_end_date` ,`created_date` ,`created_by` ,`updated_date` ,`updated_by` ) VALUES 
					(7, '2012-12-12-NO1','Lion snoope', 'best article', 'quantity', 'G1', '2', 2, 'spec ship item', 1, 1, 'code', '2012-12-12 20:20:20.000', '2012-12-12 20:20:20.000', '2012-12-12 20:20:20.000', 'system','2012-12-12 20:20:20.000', 'system' );
INSERT INTO `items` (`_id` ,`no` , `description` ,`description2` ,`unit_of_measure` ,`category_code` ,`group_code` ,`campaign_status` ,`connected_spec_ship_item` ,`unit_sales_price_eur` ,`unit_sales_price_din` ,`campaign_code` ,`cmpaign_start_date` ,`campaign_end_date` ,`created_date` ,`created_by` ,`updated_date` ,`updated_by` ) VALUES 
					(8, '2012-12-12-NO1','Article', 'best article', 'quantity', 'G1', '2', 2, 'spec ship item', 1, 1, 'code', '2012-12-12 20:20:20.000', '2012-12-12 20:20:20.000', '2012-12-12 20:20:20.000', 'system','2012-12-12 20:20:20.000', 'system' );

CREATE TABLE `visits` (
		`_id` INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL ,
		`sales_person_id` INTEGER,
		`visit_date` TEXT,
		`customer_id` INTEGER,
		`customer_name` TEXT,
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
