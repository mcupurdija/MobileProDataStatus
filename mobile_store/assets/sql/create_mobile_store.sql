-- users
CREATE TABLE `users` ( 
	`_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
	`username` TEXT,
	`pass` TEXT,
	`sales_person_id` INTEGER,
	`active` INTEGER NOT NULL  DEFAULT 0,
	`last_login` TEXT,
	`users_role_id` INTEGER
);
INSERT INTO `users` (_id, username, pass, sales_person_id, last_login, users_role_id) VALUES 
	(1, 'user', '1234', 1, datetime('now'), 1);
INSERT INTO `users` (_id, username, pass, sales_person_id, last_login, users_role_id) VALUES 
	(2, 'admin', '1234', 1, datetime('now'), 2);

	
-- user role

CREATE TABLE `users_role` (
	`_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
	`name` TEXT,
	`desc` TEXT

);

INSERT INTO `users_role` (`_id`, `name`, `desc`) VALUES (1, 'USER', 'Role for sale person');
INSERT INTO `users_role` (`_id`, `name`, `desc`) VALUES (2, 'ADMIN', 'Role for app administator');

	
	
	
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
	(1, 'IVAN', 'vlada', 'vlada', datetime('now'), 'INITIAL');
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
	`sync_object_batch` INTEGER,
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
	(1 , 'IF11-00002', 5 , '2012-11-10 08:08:00', 1 , '2012-12-20', 100290 , 100 , 5 , '2012-11-10 08:08:00', 'system', datetime('now'), 'INITIAL' );
INSERT INTO `invoices` (`_id` ,	`invoice_no`  ,	`customer_id`,`posting_date` ,`sales_person_id`,`due_date` ,`total` ,`total_left` ,	`due_date_days_left` ,`created_date` ,`created_by` ,`updated_date`,	`updated_by`) VALUES
	(2 , 'IF11-00003', 6 , '2011-11-12 14:00:00', 1 , '2011-12-15', 23900 , 22 , 2 , '2011-11-12 14:00:00', 'system', datetime('now'), 'INITIAL');
INSERT INTO `invoices` (`_id` ,	`invoice_no`  ,	`customer_id`,`posting_date` ,`sales_person_id`,`due_date` ,`total` ,`total_left` ,	`due_date_days_left` ,`created_date` ,`created_by` ,`updated_date`,	`updated_by`) VALUES
	(3 , 'IF11-00004', 7 , '2011-11-12 16:00:00', 1 , '2011-12-16', 23765, 411 , 2 , '2011-11-12 16:00:00', 'system', datetime('now'), 'INITIAL');
INSERT INTO `invoices` (`_id` ,	`invoice_no`  ,	`customer_id`,`posting_date` ,`sales_person_id`,`due_date` ,`total` ,`total_left` ,	`due_date_days_left` ,`created_date` ,`created_by` ,`updated_date`,	`updated_by`) VALUES
	(4 , 'IF11-00005', 8 , '2012-11-18 05:08:00', 1 , '2012-12-17', 76435, 100 , 5 , '2012-11-10 05:08:00', 'system', datetime('now'), 'INITIAL' );
INSERT INTO `invoices` (`_id` ,	`invoice_no`  ,	`customer_id`,`posting_date` ,`sales_person_id`,`due_date` ,`total` ,`total_left` ,	`due_date_days_left` ,`created_date` ,`created_by` ,`updated_date`,	`updated_by`) VALUES
	(5 , 'IF11-00006', 9 , '2011-11-15 15:00:00', 1 , '2011-12-18', 83765, 22 , 2 , '2011-11-12 15:00:00', 'system', datetime('now'), 'INITIAL');
INSERT INTO `invoices` (`_id` ,	`invoice_no`  ,	`customer_id`,`posting_date` ,`sales_person_id`,`due_date` ,`total` ,`total_left` ,	`due_date_days_left` ,`created_date` ,`created_by` ,`updated_date`,	`updated_by`) VALUES
	(6 , 'IF11-00007', 4 , '2011-11-16 16:00:00', 1 , '2011-12-19', 63588, 411 , 2 , '2011-11-16 16:00:00', 'system', datetime('now'), 'INITIAL');

-- customers
CREATE TABLE `customers` (
	`_id` INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL,
	`sales_person_id` INTEGER,
	`customer_no` TEXT, 
	`name` TEXT, 
	`name2` TEXT, 
	`address` TEXT, 
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
	`blocked_status` TEXT,
	`sml` TEXT,
	`internal_balance_due_lcy`REAL,
	`adopted_potential`REAL,
	`focus_customer` TEXT,
	`division` TEXT,
	`number_of_blue_coat` INTEGER,
	`number_of_grey_coat` INTEGER,
	`sync_object_batch` INTEGER,
	`sales_lcy` REAL,
	`gross_profit_pfep` REAL,
	`turnover_in_last_3m` REAL,
	`turnover_in_last_6m` REAL,
	`turnover_in_last_12m` REAL,
	`turnover_generated_3` REAL,
	`turnover_generated_2` REAL,
	`turnover_generated_1` REAL,
	`number_of_diff_items_3` INTEGER,
	`number_of_diff_items_2` INTEGER,
	`number_of_diff_items_1` INTEGER,
	`orsy_shelf_count_at_cust` INTEGER,
	`customer_12_months_plan` REAL,
	`avarage_payment_days` REAL,
	`number_of_salespersons_working_with_customer` INTEGER,
	`days_since_oldest_open_invoice` INTEGER,
	`next_15_days_invoice_due_amount` REAL,
	`next_15_days_due_invoice_count` INTEGER,
	`financial_control_status` TEXT
);
INSERT INTO  `customers` (`_id`, `sales_person_id`, `customer_no` , `name` , `name2` , 	`address`, 	`phone` , `mobile`, `global_dimension` , `credit_limit_lcy`, `payment_terms_code`, `priority`, `vat_reg_no`, `post_code`, `email`, `primary_contact_id`, `company_id`, `sml`, `adopted_potential`, `focus_customer`, `division`,`number_of_blue_coat`,`number_of_grey_coat`, `balance_lcy` , `balance_due_lcy`, `internal_balance_due_lcy`, `blocked_status`)
values ('1','1','K00001','Šule Inter Trans komerc','','Višnjicki put 22','381112442421','381648978987','101','0','10 DANA','0','106200575','11000','sule@gmail.com','','','','0','No','1','0','0','182,368,864','182,368,864','182,368,864', '0');
INSERT INTO  `customers` (`_id`, `sales_person_id`, `customer_no` , `name` , `name2` , 	`address`, 	`phone` , `mobile`, `global_dimension` , `credit_limit_lcy`, `payment_terms_code`, `priority`, `vat_reg_no`, `post_code`, `email`, `primary_contact_id`, `company_id`, `sml`, `adopted_potential`, `focus_customer`, `division`,`number_of_blue_coat`,`number_of_grey_coat`, `balance_lcy` , `balance_due_lcy`, `internal_balance_due_lcy`, `blocked_status`)
values ('2','1','K00002','Greda DOO BEOGRAD','','','','','','0','','0','','11000','','','20481943','L1','0','No','','0','0','10,200','10,200','10,200', '0');
INSERT INTO  `customers` (`_id`, `sales_person_id`, `customer_no` , `name` , `name2` , 	`address`, 	`phone` , `mobile`, `global_dimension` , `credit_limit_lcy`, `payment_terms_code`, `priority`, `vat_reg_no`, `post_code`, `email`, `primary_contact_id`, `company_id`, `sml`, `adopted_potential`, `focus_customer`, `division`,`number_of_blue_coat`,`number_of_grey_coat`, `balance_lcy` , `balance_due_lcy`, `internal_balance_due_lcy`, `blocked_status`)
values ('3','1','K00003','JAVNO PREDUZECE  VODOVOD ','','','','','','0','','0','','11000','','','17536320','','0','No','','0','0','1,334,700','1,334,700','1,334,700', '0');
INSERT INTO  `customers` (`_id`, `sales_person_id`, `customer_no` , `name` , `name2` , 	`address`, 	`phone` , `mobile`, `global_dimension` , `credit_limit_lcy`, `payment_terms_code`, `priority`, `vat_reg_no`, `post_code`, `email`, `primary_contact_id`, `company_id`, `sml`, `adopted_potential`, `focus_customer`, `division`,`number_of_blue_coat`,`number_of_grey_coat`, `balance_lcy` , `balance_due_lcy`, `internal_balance_due_lcy`, `blocked_status`)
values ('4','1','K00004','WEST BEOCAR DOO','','','','','','500','','0','','11070','','','20678593','','0','No','','0','0','320,965.39','320,965.39','320,965.39', '0');
INSERT INTO  `customers` (`_id`, `sales_person_id`, `customer_no` , `name` , `name2` , 	`address`, 	`phone` , `mobile`, `global_dimension` , `credit_limit_lcy`, `payment_terms_code`, `priority`, `vat_reg_no`, `post_code`, `email`, `primary_contact_id`, `company_id`, `sml`, `adopted_potential`, `focus_customer`, `division`,`number_of_blue_coat`,`number_of_grey_coat`, `balance_lcy` , `balance_due_lcy`, `internal_balance_due_lcy`, `blocked_status`)
values ('5','1','K00005','TELEKOM SRBIJA A.D.','','','','','','2,130,000','','0','','11000','','','','','0','No','','0','0','2,117,350','2,117,350','2,117,350', '0');
INSERT INTO  `customers` (`_id`, `sales_person_id`, `customer_no` , `name` , `name2` , 	`address`, 	`phone` , `mobile`, `global_dimension` , `credit_limit_lcy`, `payment_terms_code`, `priority`, `vat_reg_no`, `post_code`, `email`, `primary_contact_id`, `company_id`, `sml`, `adopted_potential`, `focus_customer`, `division`,`number_of_blue_coat`,`number_of_grey_coat`, `balance_lcy` , `balance_due_lcy`, `internal_balance_due_lcy`, `blocked_status`)
values ('6','1','K00006','Porshe Srbija','','','','','','0','','0','','','','','','','0','No','','0','0','-41,850','-41,850','-41,850', '0');
INSERT INTO  `customers` (`_id`, `sales_person_id`, `customer_no` , `name` , `name2` , 	`address`, 	`phone` , `mobile`, `global_dimension` , `credit_limit_lcy`, `payment_terms_code`, `priority`, `vat_reg_no`, `post_code`, `email`, `primary_contact_id`, `company_id`, `sml`, `adopted_potential`, `focus_customer`, `division`,`number_of_blue_coat`,`number_of_grey_coat`, `balance_lcy` , `balance_due_lcy`, `internal_balance_due_lcy`, `blocked_status`)
values ('7','1','K00007','Kupac zatvaranje','','','','','','0','','0','','','','','','','0','No','','0','0','100','100','100', '0');
INSERT INTO  `customers` (`_id`, `sales_person_id`, `customer_no` , `name` , `name2` , 	`address`, 	`phone` , `mobile`, `global_dimension` , `credit_limit_lcy`, `payment_terms_code`, `priority`, `vat_reg_no`, `post_code`, `email`, `primary_contact_id`, `company_id`, `sml`, `adopted_potential`, `focus_customer`, `division`,`number_of_blue_coat`,`number_of_grey_coat`, `balance_lcy` , `balance_due_lcy`, `internal_balance_due_lcy`, `blocked_status`)
values ('8','1','K00008','GoPro','','Bul. Arsenija Carn.','0112345678','','0','','','0','','','','KONT-00018','','','0','No','','0','0','0','0','0', '1');
INSERT INTO  `customers` (`_id`, `sales_person_id`, `customer_no` , `name` , `name2` , 	`address`, 	`phone` , `mobile`, `global_dimension` , `credit_limit_lcy`, `payment_terms_code`, `priority`, `vat_reg_no`, `post_code`, `email`, `primary_contact_id`, `company_id`, `sml`, `adopted_potential`, `focus_customer`, `division`,`number_of_blue_coat`,`number_of_grey_coat`, `balance_lcy` , `balance_due_lcy`, `internal_balance_due_lcy`, `blocked_status`)
values ('9','1','K00009','EXPERT-ŠPED D.O.O.','','Auto put bb','','','0','','','0','','11070','','','','','0','No','','0','0','0','0','0', '2');
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
	`division` TEXT,
	`number_of_blue_coat` INTEGER,
	`number_of_grey_coat` INTEGER,
	`job_title` TEXT,
	`sync_object_batch` INTEGER,
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
INSERT INTO `contacts` VALUES ('1','KT0001','0','Kontak broj 1',null,'Neznanih junaka BB','Beograd','11000','011111111',null,'test@test.com',null,null,null,'1',null,null,null,null,null,'2012-12-26 19:30:59','tica','2012-12-26 19:30:59','tica');
INSERT INTO `contacts` VALUES ('2','KT0002','1','Kontak broj 2',null,'Neznanih junaka BB','Beograd','11000','011111111',null,'test@test.com',null,null,null,'1',null,null,null,null,null,'2012-12-26 19:31:17','tica','2012-12-26 19:31:17','tica');
INSERT INTO `contacts` VALUES ('3','KT0003','1','Kontak broj 3',null,'Neznanih junaka BB','Beograd','11000','011111111',null,'test@test.com',null,null,null,'1',null,null,null,null,null,'2012-12-26 19:31:31','tica','2012-12-26 19:31:31','tica');

-- items
CREATE TABLE `items` (
	`_id` INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL ,
	`item_no` TEXT,
	`description` TEXT,
	`description2` TEXT,
	`unit_of_measure` TEXT,
	`category_code` TEXT,
	`group_code` TEXT,
	`campaign_status` TEXT,
	`overstock_status` TEXT,
	`connected_spec_ship_item` TEXT,
	`unit_sales_price_eur` REAL,
	`unit_sales_price_din` REAL,
	`campaign_code` TEXT,
	`cmpaign_start_date` TEXT,
	`campaign_end_date` TEXT,
	`sync_object_batch` INTEGER,
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
INSERT INTO `items` (`_id` ,`item_no` , `description`, `unit_of_measure` ,`category_code` ,`group_code` ,`campaign_status` , `campaign_code` ,`connected_spec_ship_item`)
values ('1','0 376 8  12','Navrtka udarna M8 X H11mm','KOM','ALAT','ELEKTROOPR','0','0','');
INSERT INTO `items` (`_id` ,`item_no` , `description`, `unit_of_measure` ,`category_code` ,`group_code` ,`campaign_status` , `campaign_code` ,`connected_spec_ship_item`)
values ('2','0 456 11','Vezica','KOM','VEZICE','MALE','0','0','');
INSERT INTO `items` (`_id` ,`item_no` , `description`, `unit_of_measure` ,`category_code` ,`group_code` ,`campaign_status` , `campaign_code` ,`connected_spec_ship_item`)
values ('3','0 456 111','Set kljuceva','KOM','ALAT','SETOVI','0','0','');
INSERT INTO `items` (`_id` ,`item_no` , `description`, `unit_of_measure` ,`category_code` ,`group_code` ,`campaign_status` , `campaign_code` ,`connected_spec_ship_item`)
values ('4','0 456 222','Francuski kljuc','KOM','ALAT','ELEKTROOPR','0','0','');
INSERT INTO `items` (`_id` ,`item_no` , `description`, `unit_of_measure` ,`category_code` ,`group_code` ,`campaign_status` , `campaign_code` ,`connected_spec_ship_item`)
values ('5','0 587 760150','Lepezasti brusni tockic, G1500','KOM','','','1','1','');
INSERT INTO `items` (`_id` ,`item_no` , `description`, `unit_of_measure` ,`category_code` ,`group_code` ,`campaign_status` , `campaign_code` ,`connected_spec_ship_item`)
values ('6','0 614 787305','Umetak 5/16, TX30, L50mm','KOM','','','0','0','');
INSERT INTO `items` (`_id` ,`item_no` , `description`, `unit_of_measure` ,`category_code` ,`group_code` ,`campaign_status` , `campaign_code` ,`connected_spec_ship_item`)
values ('7','0 614 788250','Umetak, 10mm, TX50, L120mm','KOM','','','0','0','');
INSERT INTO `items` (`_id` ,`item_no` , `description`, `unit_of_measure` ,`category_code` ,`group_code` ,`campaign_status` , `campaign_code` ,`connected_spec_ship_item`)
values ('8','0 681 001001','Paropropusna folija WUTOP TRIO','KOM','','','0','0','');
INSERT INTO `items` (`_id` ,`item_no` , `description`, `unit_of_measure` ,`category_code` ,`group_code` ,`campaign_status` , `campaign_code` ,`connected_spec_ship_item`)
values ('9','0 893 5562','Cistac hladnjaka, 250ml','KOM','','','0','0','');
INSERT INTO `items` (`_id` ,`item_no` , `description`, `unit_of_measure` ,`category_code` ,`group_code` ,`campaign_status` , `campaign_code` ,`connected_spec_ship_item`)
values ('10','0 893 567','Diesel aditiv CR, 300 ml','KOM','','','0','0','');
INSERT INTO `items` (`_id` ,`item_no` , `description`, `unit_of_measure` ,`category_code` ,`group_code` ,`campaign_status` , `campaign_code` ,`connected_spec_ship_item`)
values ('11','0 984 160110','Mesavina ulja akcija','KOM','MAZIVA','MAZHID','0','0','');
INSERT INTO `items` (`_id` ,`item_no` , `description`, `unit_of_measure` ,`category_code` ,`group_code` ,`campaign_status` , `campaign_code` ,`connected_spec_ship_item`)
values ('12','0 991 4546','Ulje za hidrauliku 20L','KOM','MAZIVA','MAZHID','0','0','');
INSERT INTO `items` (`_id` ,`item_no` , `description`, `unit_of_measure` ,`category_code` ,`group_code` ,`campaign_status` , `campaign_code` ,`connected_spec_ship_item`)
values ('13','0 992 3546','Ulje za kocnice','KOM','MAZIVA','MAZHID','0','0','');
INSERT INTO `items` (`_id` ,`item_no` , `description`, `unit_of_measure` ,`category_code` ,`group_code` ,`campaign_status` , `campaign_code` ,`connected_spec_ship_item`)
values ('14','0 992 9001','Sijalica za farove','KOM','RASVETA','AUTOSIJA','0','0','');
INSERT INTO `items` (`_id` ,`item_no` , `description`, `unit_of_measure` ,`category_code` ,`group_code` ,`campaign_status` , `campaign_code` ,`connected_spec_ship_item`)
values ('15','007 007','Nestandardan artikal','KOM','','','0','0','');
INSERT INTO `items` (`_id` ,`item_no` , `description`, `unit_of_measure` ,`category_code` ,`group_code` ,`campaign_status` , `campaign_code` ,`connected_spec_ship_item`)
values ('16','007 008','Min kolicina artikal','KOM','ALAT','ELEKTROOPR','0','0','');
INSERT INTO `items` (`_id` ,`item_no` , `description`, `unit_of_measure` ,`category_code` ,`group_code` ,`campaign_status` , `campaign_code` ,`connected_spec_ship_item`)
values ('17','1111','Olovka','KOM','','','1','1','0 376 8  12');
INSERT INTO `items` (`_id` ,`item_no` , `description`, `unit_of_measure` ,`category_code` ,`group_code` ,`campaign_status` , `campaign_code` ,`connected_spec_ship_item`)
values ('18','20','Artikal 20 za dodatni trosak','','','','0','0','');
INSERT INTO `items` (`_id` ,`item_no` , `description`, `unit_of_measure` ,`category_code` ,`group_code` ,`campaign_status` , `campaign_code` ,`connected_spec_ship_item`)
values ('19','2112','Gume','KOM','ALAT','ELEKTROOPR','0','0','');
INSERT INTO `items` (`_id` ,`item_no` , `description`, `unit_of_measure` ,`category_code` ,`group_code` ,`campaign_status` , `campaign_code` ,`connected_spec_ship_item`)
values ('20','345','dsfadf dfasfd','KOM','ALAT','SETOVI','0','0','');
INSERT INTO `items` (`_id` ,`item_no` , `description`, `unit_of_measure` ,`category_code` ,`group_code` ,`campaign_status` , `campaign_code` ,`connected_spec_ship_item`)
values ('21','60','Artikal 60 za dodatni trosak','','','','0','0','');
INSERT INTO `items` (`_id` ,`item_no` , `description`, `unit_of_measure` ,`category_code` ,`group_code` ,`campaign_status` , `campaign_code` ,`connected_spec_ship_item`)
values ('22','612','tocak','KOM','','','0','0','');
INSERT INTO `items` (`_id` ,`item_no` , `description`, `unit_of_measure` ,`category_code` ,`group_code` ,`campaign_status` , `campaign_code` ,`connected_spec_ship_item`)
values ('23','83','Gume','KOM','','','0','0','');
INSERT INTO `items` (`_id` ,`item_no` , `description`, `unit_of_measure` ,`category_code` ,`group_code` ,`campaign_status` , `campaign_code` ,`connected_spec_ship_item`)
values ('24','JKL','Min kolicina artikal','KOM','MAZIVA','MAZHID','1','1','0 456 11');
INSERT INTO `items` (`_id` ,`item_no` , `description`, `unit_of_measure` ,`category_code` ,`group_code` ,`campaign_status` , `campaign_code` ,`connected_spec_ship_item`)
values ('25','V123','Brankov artikal','KOM','MAZIVA','PENA','2','2','');
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
	`sync_object_batch` INTEGER,
	`visit_type` INTEGER,
	`is_sent` INTEGER,
	`created_date` TEXT,
	`created_by` TEXT,
	`updated_date` TEXT,
	`updated_by` TEXT, 
	UNIQUE (`sales_person_id`,`visit_date`,`customer_id`)
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
INSERT INTO `visits` VALUES ('1','1','2013-12-18 00:00:00','1',null,null,'1','2012-12-18 00:00:00','2012-12-18 00:00:00',null,'dada',null,1,1, '2012-12-18 01:11:12','vlada','2012-12-18 01:11:12','vlada');
INSERT INTO `visits` VALUES ('2','1','2013-02-18 00:00:00','2',null,null,'1000','2012-12-18 00:00:00','2012-12-18 00:00:00',null,'dada',null,1,1,'2012-12-18 01:11:12','vlada','2012-12-18 01:11:12','vlada');
INSERT INTO `visits` VALUES ('3','1','2013-01-18 00:00:00','3',null,null,'1','2012-12-18 00:00:00','2012-12-18 00:00:00',null,'dada',null,1,1,'2012-12-18 01:11:12','vlada','2012-12-18 01:11:12','vlada');
INSERT INTO `visits` VALUES ('4','1','2012-12-19 00:00:00','4',null,null,'1000','2012-12-18 00:00:00','2012-12-18 00:00:00',null,'dada',null,1,1, '2012-12-18 01:11:12','vlada','2012-12-18 01:11:12','vlada');
INSERT INTO `visits` VALUES ('5','1','2012-12-17 00:00:00','5',null,null,'1','2012-12-18 00:00:00','2012-12-18 00:00:00',null,'dada',null,1,1, '2012-12-18 01:11:12','vlada','2012-12-18 01:11:12','vlada');
INSERT INTO `visits` VALUES ('6','1','2012-12-11 00:00:00','6',null,null,'1000','2012-12-18 00:00:00','2012-12-18 00:00:00',null,'dada',null,1,1, '2012-12-18 01:11:12','vlada','2012-12-18 01:11:12','vlada');

CREATE TABLE `sale_orders` (
	`_id` INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL,
	`sales_order_no` TEXT,
	`document_type` INTEGER,
	`customer_id` INTEGER,
	`order_date` TEXT,
	`location_code` TEXT,
	`shortcut_dimension_1_code` TEXT,
	`currency_code` INTEGER,
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
	`cust_uses_transit_cust` INTEGER,
	`sales_person_id` INTEGER,
	`sell_to_address_id` INTEGER,
	`shipp_to_address_id` INTEGER,
	`contact_id` INTEGER,
	`contact_name` TEXT,
	`contact_phone` TEXT,
	`payment_option` INTEGER,
	`check_status_phone` TEXT,
	`total` REAL,
	`total_discount` REAL,
	`total_pdv` REAL,
	`total_items` REAL,
	`hide_rebate` INTEGER DEFAULT 0,
	`further_sale` INTEGER DEFAULT 0,
	`customer_business_unit_code` TEXT DEFAULT NULL,
	`note1` TEXT,
	`note2` TEXT,
	`note3` TEXT,
	`sync_object_batch` INTEGER,
	`created_date` TEXT,
	`created_by` TEXT,
	`updated_date` TEXT,
	`updated_by` TEXT
);

INSERT INTO `sale_orders` (	`_id` ,	`sales_order_no` ,	`document_type` ,	`customer_id` ,	`order_date` ,	`location_code` ,	`shortcut_dimension_1_code` ,	`currency_code` ,	`external_document_no` ,	`quote_no` ,	`backorder_shipment_status` ,	`order_status_for_shipment` ,	`fin_control_status` ,`order_condition_status` ,	`used_credit_limit_by_employee` ,	`order_value_status` ,	`quote_realized_status` ,	`special_quote` ,	`quote_valid_date_to` ,	`cust_uses_transit_cust` ,	`sales_person_id` ,	`sell_to_address_id`, `shipp_to_address_id`, `contact_id`, `contact_name`, `contact_phone` , `payment_option` ,	`check_status_phone` ,	`total` ,	`total_discount` ,	`total_pdv` ,	`total_items` ,	`hide_rebate` ,	`further_sale`,	`note1` ,	`note2` ,	`note3` ,	`created_date` ,	`created_by` ,	`updated_date` ,	`updated_by`) VALUES
(1, 'POR12-00001', 0,2, '2012-06-12 20:20:20', '001', 'SA1', 1, 'ext doc', 'quote no', 2,2,2,2,'used limitation', 2,2,2, '2012-12-12 20:20:20', null, 1, 3, null,2,'contact name','06457575',0, 'startus phone', 2000, 1000, 5, 200, 0,0, 'note1', 'note2', 'note3', '2012-12-12 20:20:20','sys', '2012-12-12 20:20:20', 'sys');
INSERT INTO `sale_orders` (	`_id` ,	`sales_order_no` ,	`document_type` ,	`customer_id` ,	`order_date` ,	`location_code` ,	`shortcut_dimension_1_code` ,	`currency_code` ,	`external_document_no` ,	`quote_no` ,	`backorder_shipment_status` ,	`order_status_for_shipment` ,	`fin_control_status` ,`order_condition_status` ,	`used_credit_limit_by_employee` ,	`order_value_status` ,	`quote_realized_status` ,	`special_quote` ,	`quote_valid_date_to` ,	`cust_uses_transit_cust` ,	`sales_person_id` ,	`sell_to_address_id`, `shipp_to_address_id`, `contact_id`, `contact_name`, `contact_phone` , `payment_option` ,	`check_status_phone` ,	`total` ,	`total_discount` ,	`total_pdv` ,	`total_items` ,	`hide_rebate` ,	`further_sale`,	`note1` ,	`note2` ,	`note3` ,	`created_date` ,	`created_by` ,	`updated_date` ,	`updated_by`) VALUES
(2, 'POR12-00002', 0,2, '2012-12-12 10:20:20', '001', 'SA1', 1, 'ext doc', 'quote no', 2,2,2,2,'used limitation', 2,2,2, '2012-12-12 20:20:20', null, 1, 3, null,2,'contact name','06457575',0, 'startus phone', 2500, 1000, 5, 200, 0,0, 'note1', 'note2', 'note3', '2012-12-12 20:20:20','sys', '2012-12-12 20:20:20', 'sys');
INSERT INTO `sale_orders` (	`_id` ,	`sales_order_no` ,	`document_type` ,	`customer_id` ,	`order_date` ,	`location_code` ,	`shortcut_dimension_1_code` ,	`currency_code` ,	`external_document_no` ,	`quote_no` ,	`backorder_shipment_status` ,	`order_status_for_shipment` ,	`fin_control_status` ,`order_condition_status` ,	`used_credit_limit_by_employee` ,	`order_value_status` ,	`quote_realized_status` ,	`special_quote` ,	`quote_valid_date_to` ,	`cust_uses_transit_cust` ,	`sales_person_id` ,	`sell_to_address_id`, `shipp_to_address_id`, `contact_id`, `contact_name`, `contact_phone` , `payment_option` ,	`check_status_phone` ,	`total` ,	`total_discount` ,	`total_pdv` ,	`total_items` ,	`hide_rebate` ,	`further_sale`,	`note1` ,	`note2` ,	`note3` ,	`created_date` ,	`created_by` ,	`updated_date` ,	`updated_by`) VALUES
(3, 'POR12-00003', 1,2, '2010-10-12 10:20:20', '001', 'SA1', 1, 'ext doc', 'quote no', 2,2,2,2,'used limitation', 2,2,2, '2012-12-12 20:20:20', null, 1, 3, null,2,'contact name','06457575',0, 'startus phone', 1000, 1000, 5, 200, 0,0, 'note1', 'note2', 'note3', '2012-12-12 20:20:20','sys', '2012-12-12 20:20:20', 'sys');
INSERT INTO `sale_orders` (	`_id` ,	`sales_order_no` ,	`document_type` ,	`customer_id` ,	`order_date` ,	`location_code` ,	`shortcut_dimension_1_code` ,	`currency_code` ,	`external_document_no` ,	`quote_no` ,	`backorder_shipment_status` ,	`order_status_for_shipment` ,	`fin_control_status` ,`order_condition_status` ,	`used_credit_limit_by_employee` ,	`order_value_status` ,	`quote_realized_status` ,	`special_quote` ,	`quote_valid_date_to` ,	`cust_uses_transit_cust` ,	`sales_person_id` ,	`sell_to_address_id`, `shipp_to_address_id`, `contact_id`, `contact_name`, `contact_phone` , `payment_option` ,	`check_status_phone` ,	`total` ,	`total_discount` ,	`total_pdv` ,	`total_items` ,	`hide_rebate` ,	`further_sale`,	`note1` ,	`note2` ,	`note3` ,	`created_date` ,	`created_by` ,	`updated_date` ,	`updated_by`) VALUES
(4, 'POR12-00004', 1,1, '2012-12-12 00:20:20', '001', 'SA2', 1, 'ext doc', 'quote no', 2,2,2,2,'used limitation', 2,2,2, '2012-12-12 20:20:20', null, 1, 1, null,1,'contact name','06457575',1, 'startus phone', 22000, 1000, 5, 200, 0,0, 'note1', 'note2', 'note3', '2012-12-12 20:20:20','sys', '2012-12-12 20:20:20', 'sys');
INSERT INTO `sale_orders` (	`_id` ,	`sales_order_no` ,	`document_type` ,	`customer_id` ,	`order_date` ,	`location_code` ,	`shortcut_dimension_1_code` ,	`currency_code` ,	`external_document_no` ,	`quote_no` ,	`backorder_shipment_status` ,	`order_status_for_shipment` ,	`fin_control_status` ,`order_condition_status` ,	`used_credit_limit_by_employee` ,	`order_value_status` ,	`quote_realized_status` ,	`special_quote` ,	`quote_valid_date_to` ,	`cust_uses_transit_cust` ,	`sales_person_id` ,	`sell_to_address_id`, `shipp_to_address_id`, `contact_id`, `contact_name`, `contact_phone` , `payment_option` ,	`check_status_phone` ,	`total` ,	`total_discount` ,	`total_pdv` ,	`total_items` ,	`hide_rebate` ,	`further_sale`,	`note1` ,	`note2` ,	`note3` ,	`created_date` ,	`created_by` ,	`updated_date` ,	`updated_by`) VALUES
(5, 'POR12-00005', 1,1, '2010-12-12 08:20:20', '001', 'SA2', 0, 'ext doc', 'quote no', 2,2,2,2,'used limitation', 2,2,2, '2012-12-12 20:20:20', null, 1, 1, null,1,'contact name','06457575',1, 'startus phone', 8000, 1000, 5, 200, 0,0, 'note1', 'note2', 'note3', '2012-12-12 20:20:20','sys', '2012-12-12 20:20:20', 'sys');
INSERT INTO `sale_orders` (	`_id` ,	`sales_order_no` ,	`document_type` ,	`customer_id` ,	`order_date` ,	`location_code` ,	`shortcut_dimension_1_code` ,	`currency_code` ,	`external_document_no` ,	`quote_no` ,	`backorder_shipment_status` ,	`order_status_for_shipment` ,	`fin_control_status` ,`order_condition_status` ,	`used_credit_limit_by_employee` ,	`order_value_status` ,	`quote_realized_status` ,	`special_quote` ,	`quote_valid_date_to` ,	`cust_uses_transit_cust` ,	`sales_person_id` ,	`sell_to_address_id`, `shipp_to_address_id`, `contact_id`, `contact_name`, `contact_phone` , `payment_option` ,	`check_status_phone` ,	`total` ,	`total_discount` ,	`total_pdv` ,	`total_items` ,	`hide_rebate` ,	`further_sale`,	`note1` ,	`note2` ,	`note3` ,	`created_date` ,	`created_by` ,	`updated_date` ,	`updated_by`) VALUES
(6, 'POR12-00006', 1,1, '2011-12-12 09:20:20', '001', 'SA2', 0, 'ext doc', 'quote no', 2,2,2,2,'used limitation', 2,2,2, '2012-12-12 20:20:20', null, 1, 1, null,1,'contact name','06457575',1, 'startus phone', 6000, 1000, 5, 200, 0,0, 'note1', 'note2', 'note3', '2012-12-12 20:20:20','sys', '2012-12-12 20:20:20', 'sys');


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
	`quantity_available` REAL,
	`unit_of_measure` TEXT,
	`price` REAL,
	`min_discount` REAL,
	`max_discount` REAL,
	`real_discount` REAL,
	`line_total` REAL,
	`line_origin` TEXT,
	`price_eur` REAL,
	`quote_refused_status` INTEGER,
	`campaign_status` INTEGER,
	`verify_status` INTEGER,
	`price_discount_status` INTEGER,
	`quantity_available_status` INTEGER,
	`backorder_status` INTEGER,
	`available_to_whole_shipment` INTEGER,
	`sync_object_batch` INTEGER,
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
INSERT INTO `sale_order_lines` VALUES ('1','1','1','1','23','25.8','KOM','22.33','10','20','12',null,null,'23456','0','0',null,null,null,'0','0',null,'2012-12-19 15:29:42','tica','2012-12-19 15:29:42','tica');
INSERT INTO `sale_order_lines` VALUES ('2','1','2','2','100','41','KOM','1000','10','20','18',null,null,'2233','0','0',null,null,null,'0','0',null,'2012-12-19 15:29:42','tica','2012-12-19 15:29:42','tica');
INSERT INTO `sale_order_lines` VALUES ('3','1','3','3','2','21.8','KOM','2023','10','20','12',null,null,'1256','0','0',null,null,null,'0','0',null,'2012-12-19 15:29:42','tica','2012-12-19 15:29:42','tica');
INSERT INTO `sale_order_lines` VALUES ('4','1','4','4','1','130','KOM','512.33','10','20','18',null,null,'2345','0','0',null,null,null,'0','0',null,'2012-12-19 15:29:42','tica','2012-12-19 15:29:42','tica');

INSERT INTO `sale_order_lines` VALUES ('5','2','1','5','2','130','KOM','23','10','20','12',null,null,'23456','0','0',null,null,null,'0','0',null,'2012-12-19 15:29:42','tica','2012-12-19 15:29:42','tica');
INSERT INTO `sale_order_lines` VALUES ('6','2','2','6','2','130','KOM','214','10','20','18',null,null,'2233','0','0',null,null,null,'0','0',null,'2012-12-19 15:29:42','tica','2012-12-19 15:29:42','tica');
INSERT INTO `sale_order_lines` VALUES ('7','2','3','7','2','130','KOM','2023','10','20','12',null,null,'1256','0','0',null,null,null,'0','0',null,'2012-12-19 15:29:42','tica','2012-12-19 15:29:42','tica');
INSERT INTO `sale_order_lines` VALUES ('8','2','4','8','2','130','KOM','512.33','10','20','18',null,null,'2345','0','0',null,null,null,'0','0',null,'2012-12-19 15:29:42','tica','2012-12-19 15:29:42','tica');

INSERT INTO `sale_order_lines` VALUES ('9','3','1','1','2','130','KOM','23','10','20','12',null,null,'23456','0','0',null,null,null,'0','0',null,'2012-12-19 15:29:42','tica','2012-12-19 15:29:42','tica');
INSERT INTO `sale_order_lines` VALUES ('10','3','2','3','2','130','KOM','214','10','20','18',null,null,'2233','0','0',null,null,null,'0','0',null,'2012-12-19 15:29:42','tica','2012-12-19 15:29:42','tica');
INSERT INTO `sale_order_lines` VALUES ('11','3','3','4','2','130','KOM','2023','10','20','12',null,null,'1256','0','0',null,null,null,'0','0',null,'2012-12-19 15:29:42','tica','2012-12-19 15:29:42','tica');
INSERT INTO `sale_order_lines` VALUES ('12','3','4','7','2','130','KOM','512.33','10','20','18',null,null,'2345','0','0',null,null,null,'0','0',null,'2012-12-19 15:29:42','tica','2012-12-19 15:29:42','tica');

INSERT INTO `sale_order_lines` VALUES ('13','4','1','10','2','130','KOM','23','10','20','12',null,null,'23456','0','0',null,null,null,'0','0',null,'2012-12-19 15:29:42','tica','2012-12-19 15:29:42','tica');
INSERT INTO `sale_order_lines` VALUES ('14','4','2','13','2','130','KOM','214','10','20','18',null,null,'2233','0','0',null,null,null,'0','0',null,'2012-12-19 15:29:42','tica','2012-12-19 15:29:42','tica');
INSERT INTO `sale_order_lines` VALUES ('15','4','3','14','2','130','KOM','2023','10','20','12',null,null,'1256','0','0',null,null,null,'0','0',null,'2012-12-19 15:29:42','tica','2012-12-19 15:29:42','tica');
INSERT INTO `sale_order_lines` VALUES ('16','4','4','15','2','130','KOM','512.33','10','20','18',null,null,'2345','0','0',null,null,null,'0','0',null,'2012-12-19 15:29:42','tica','2012-12-19 15:29:42','tica');

CREATE TABLE `sync_logs` (
	`_id` INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL,
	`sync_object_name` TEXT,
	`sync_object_id` TEXT,
	`sync_object_status` TEXT,
	`sync_object_batch` INTEGER,
	`created_date` TEXT,
	`created_by` TEXT,
	`updated_date` TEXT,
	`updated_by` TEXT
);
CREATE TRIGGER IF NOT EXISTS "log_new_sync_log" AFTER INSERT ON "sync_logs" 
BEGIN 
	update sync_logs set 
	created_date = datetime('now'),
	created_by = (select username from users where active = 1)
	where _id = new._id; 
END;
CREATE TRIGGER IF NOT EXISTS "log_sync_log_change" AFTER UPDATE ON "sync_logs" 
BEGIN 
	update sync_logs set 
	updated_date = datetime('now'),
	updated_by = (select username from users where active = 1)
	where _id = new._id; 
END;

INSERT INTO `sync_logs` (`_id`,`sync_object_name`,`sync_object_id`,`sync_object_status`,`sync_object_batch`, `updated_date` ) VALUES
(1, 'ItemsSyncObject', 'ItemsSyncObject', 'FAILED', 2,'2012-12-19 15:29:42' );
INSERT INTO `sync_logs` (`_id`,`sync_object_name`,`sync_object_id`,`sync_object_status`,`sync_object_batch`, `updated_date` ) VALUES
(2, 'ItemsSyncObject', 'ItemsSyncObject', 'FAILED', 5, '2012-12-19 15:29:42');
INSERT INTO `sync_logs` (`_id`,`sync_object_name`,`sync_object_id`,`sync_object_status`,`sync_object_batch`, `updated_date` ) VALUES
(3, 'ItemsSyncObject', 'ItemsSyncObject', 'SUCCESS', 50, '2012-12-19 15:29:42');

CREATE TABLE `customer_addresses` (
	`_id` INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL,
	`address_no` TEXT,
	`customer_no` TEXT,
	`address` TEXT,
	`city` TEXT,
	`contact` TEXT,
	`phone_no` TEXT,
	`post_code` TEXT,
	`created_date` TEXT,
	`created_by` TEXT,
	`updated_date` TEXT,
	`updated_by` TEXT
);
CREATE TRIGGER IF NOT EXISTS "log_new_customer_address" AFTER INSERT ON "customer_addresses" 
BEGIN 
	update customer_addresses set 
	created_date = datetime('now'),
	created_by = (select username from users where active = 1)
	where _id = new._id; 
END;
CREATE TRIGGER IF NOT EXISTS "log_customer_address_change" AFTER UPDATE ON "customer_addresses" 
BEGIN 
	update customer_addresses set 
	updated_date = datetime('now'),
	updated_by = (select username from users where active = 1)
	where _id = new._id; 
END;
INSERT INTO `customer_addresses` (`_id`, `address_no`, `customer_no`, `address`, `city`, `contact`, `phone_no`, `post_code`) VALUES ('1','AD00001','K00001','Koste Aba 9','Valjevo','Test1','1234567','14000');
INSERT INTO `customer_addresses` (`_id`, `address_no`, `customer_no`, `address`, `city`, `contact`, `phone_no`, `post_code`) VALUES ('2','AD00002','K00001','Test2 Abrasevica 9','Beograd','Test1','1234567','11000');
INSERT INTO `customer_addresses` (`_id`, `address_no`, `customer_no`, `address`, `city`, `contact`, `phone_no`, `post_code`) VALUES ('3','AD00003','K00002','Test1 9','Valjevo','Test1','1234567','14000');
INSERT INTO `customer_addresses` (`_id`, `address_no`, `customer_no`, `address`, `city`, `contact`, `phone_no`, `post_code`) VALUES ('4','AD00004','K00002','Test2 vica 9','Valjevo','Test1','1234567','14000');
INSERT INTO `customer_addresses` (`_id`, `address_no`, `customer_no`, `address`, `city`, `contact`, `phone_no`, `post_code`) VALUES ('5','AD00005','K00003','Koste Abrasevica 9','Beograd','Test1','1234567','11000');
INSERT INTO `customer_addresses` (`_id`, `address_no`, `customer_no`, `address`, `city`, `contact`, `phone_no`, `post_code`) VALUES ('6','AD00006','K00004','Test1 9','Beograd','Test1','1234567','11000');
INSERT INTO `customer_addresses` (`_id`, `address_no`, `customer_no`, `address`, `city`, `contact`, `phone_no`, `post_code`) VALUES ('7','AD00007','K00005','Test2 Abrasevica 9','Beograd','Test1','1234567','11000');

CREATE TABLE `invoice_lines` (
	`_id` INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL,
	`invoices_id` INTEGER,
	`line_no` INTEGER,
	`customer_id` INTEGER,
	`type` INTEGER,
	`no` INTEGER,
	`location_code` TEXT,
	`description` TEXT,
	`quantity` REAL,
	`unit_price` REAL,
	`vat_percent` REAL,
	`line_discount_percent` REAL,
	`line_discount_amount` REAL,
	`amount_including_vat` REAL,
	`inv_discount_amount` REAL,
	`unit_of_measure_code` REAL,
	`price_include_vat` INTEGER,
	`created_date` TEXT,
	`created_by` TEXT,
	`updated_date` TEXT,
	`updated_by` TEXT
);

CREATE TRIGGER IF NOT EXISTS "log_new_invoice_line" AFTER INSERT ON "invoice_lines" 
BEGIN 
	update invoice_lines set 
	created_date = datetime('now'),
	created_by = (select username from users where active = 1)
	where _id = new._id; 
END;
CREATE TRIGGER IF NOT EXISTS "log_invoice_line_change" AFTER UPDATE ON "invoice_lines" 
BEGIN 
	update invoice_lines set 
	updated_date = datetime('now'),
	updated_by = (select username from users where active = 1)
	where _id = new._id; 
END;

INSERT INTO  `invoice_lines`  (`_id`,`invoices_id`,`line_no`,`customer_id`,`type`,	`no`,`location_code`,`description`,`quantity`,`unit_price`,`vat_percent`,`line_discount_percent`,`line_discount_amount`,`amount_including_vat`,`inv_discount_amount`,`unit_of_measure_code`,`price_include_vat`) VALUES
(1, 2, 3, 1, 1, 'noo', 'location_code', 'Description', '123', '213', '32', '215', '21', '222', '123', '1', '1' );
INSERT INTO  `invoice_lines`  (`_id`,`invoices_id`,`line_no`,`customer_id`,`type`,	`no`,`location_code`,`description`,`quantity`,`unit_price`,`vat_percent`,`line_discount_percent`,`line_discount_amount`,`amount_including_vat`,`inv_discount_amount`,`unit_of_measure_code`,`price_include_vat`) VALUES
(2, 2, 4, 1, 2, 'noo 2', 'location_code second', 'Description', '12', '122', '32', '21', '21', '222', '123', '1', '1' );
INSERT INTO  `invoice_lines`  (`_id`,`invoices_id`,`line_no`,`customer_id`,`type`,	`no`,`location_code`,`description`,`quantity`,`unit_price`,`vat_percent`,`line_discount_percent`,`line_discount_amount`,`amount_including_vat`,`inv_discount_amount`,`unit_of_measure_code`,`price_include_vat`) VALUES
(3, 3, 6, 1, 2, 'noo 2', 'location_code second', 'Description', '12', '2434', '32', '21', '21', '222', '123', '1', '1' );
INSERT INTO  `invoice_lines`  (`_id`,`invoices_id`,`line_no`,`customer_id`,`type`,	`no`,`location_code`,`description`,`quantity`,`unit_price`,`vat_percent`,`line_discount_percent`,`line_discount_amount`,`amount_including_vat`,`inv_discount_amount`,`unit_of_measure_code`,`price_include_vat`) VALUES
(4, 1, 3, 1, 1, 'noo', 'location_code', 'Description', '123', '213', '32', '215', '21', '222', '123', '1', '1' );
INSERT INTO  `invoice_lines`  (`_id`,`invoices_id`,`line_no`,`customer_id`,`type`,	`no`,`location_code`,`description`,`quantity`,`unit_price`,`vat_percent`,`line_discount_percent`,`line_discount_amount`,`amount_including_vat`,`inv_discount_amount`,`unit_of_measure_code`,`price_include_vat`) VALUES
(5, 1, 4, 1, 2, 'noo 2', 'location_code second', 'Description', '12', '122', '32', '21', '21', '222', '123', '1', '1' );
INSERT INTO  `invoice_lines`  (`_id`,`invoices_id`,`line_no`,`customer_id`,`type`,	`no`,`location_code`,`description`,`quantity`,`unit_price`,`vat_percent`,`line_discount_percent`,`line_discount_amount`,`amount_including_vat`,`inv_discount_amount`,`unit_of_measure_code`,`price_include_vat`) VALUES
(6, 2, 6, 1, 2, 'noo 2', 'location_code second', 'Description', '12', '2434', '32', '21', '21', '222', '123', '1', '1' );


CREATE TABLE `electronic_card_customer` (
	`_id` INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL,
	`customer_id` INTEGER,
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
	`created_date` TEXT,
	`created_by` TEXT,
	`updated_date` TEXT,
	`updated_by` TEXT
);

CREATE TRIGGER IF NOT EXISTS "log_new_electronic_card_customer" AFTER INSERT ON "electronic_card_customer" 
BEGIN 
	update electronic_card_customer set 
	created_date = datetime('now'),
	created_by = (select username from users where active = 1)
	where _id = new._id; 
END;
CREATE TRIGGER IF NOT EXISTS "log_electronic_card_customer_change" AFTER UPDATE ON "electronic_card_customer" 
BEGIN 
	update electronic_card_customer set 
	updated_date = datetime('now'),
	updated_by = (select username from users where active = 1)
	where _id = new._id; 
END;

CREATE TABLE `customer_trade_agreement` (
	`_id` INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL,
	`customer_id` INTEGER,
	`entry_type` TEXT, 
	`code` TEXT,
	`minimum_quantity` INTEGER,
	`starting_date` TEXT, 
	`ending_date` TEXT,
	`actual_discount` REAL,
	`created_date` TEXT,
	`created_by` TEXT,
	`updated_date` TEXT,
	`updated_by` TEXT
);

CREATE TRIGGER IF NOT EXISTS "log_new_customer_trade_agreement" AFTER INSERT ON "customer_trade_agreement" 
BEGIN 
	update customer_trade_agreement set 
	created_date = datetime('now'),
	created_by = (select username from users where active = 1)
	where _id = new._id; 
END;
CREATE TRIGGER IF NOT EXISTS "log_customer_trade_agreement_change" AFTER UPDATE ON "customer_trade_agreement" 
BEGIN 
	update customer_trade_agreement set 
	updated_date = datetime('now'),
	updated_by = (select username from users where active = 1)
	where _id = new._id; 
END;
