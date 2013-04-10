-- upgrade older version to 156
-- this is first upgrade file
-- date 10.4.2013.

-- visits
CREATE TABLE `temp_visits` (
	`_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
	`sales_person_id` INTEGER,
	`visit_date` TEXT,
	`customer_id` INTEGER,
	`potential_customer` INTEGER DEFAULT 0,
	`line_no` INTEGER,
	`visit_type` INTEGER DEFAULT 0,
	`entry_type` TEXT,
	`odometer` INTEGER,
	`departure_time` TEXT,
	`arrival_time` TEXT,
	`visit_result` INTEGER,
	`note` TEXT,
	`sync_object_batch` INTEGER,
	`visit_status` INTEGER DEFAULT 0,
	`is_sent` INTEGER DEFAULT 1,
	`is_deleted` INTEGER DEFAULT 0,
	`created_date` TEXT,
	`created_by` TEXT,
	`updated_date` TEXT,
	`updated_by` TEXT
);
INSERT INTO temp_visits SELECT * FROM visits;
DROP TABLE visits;
ALTER TABLE temp_visits RENAME TO visits;