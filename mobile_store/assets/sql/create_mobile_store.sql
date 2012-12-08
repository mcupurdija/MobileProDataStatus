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