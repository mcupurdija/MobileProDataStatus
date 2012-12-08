CREATE TABLE `USERS` ( 
       `_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
       `username` TEXT,
       `pass` TEXT,
       `sales_person_id` INTEGER,
       `last_login` TEXT
);
INSERT INTO `USERS` (username,pass,sales_person_id, last_login) VALUES ('tica', 'tica', 1, 'yesterday');
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