-- customers
CREATE TABLE `temp_customers` (
	`_id` INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL,
	`sales_person_id` INTEGER,
	`customer_no` TEXT, 
	`name` TEXT, 
	`name2` TEXT, 
	`address` TEXT,
	`city` TEXT,
	`post_code` TEXT, 
	`phone` TEXT, 
	`mobile` TEXT, 
	`email` TEXT, 
	`company_id` TEXT, 
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
	`contact_company_no` TEXT,
	`sync_object_batch` INTEGER,
	`turnover_ytm` REAL,
	`gross_profit_pfep` REAL,
	`apr_customer_turnover` REAL,
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
	`financial_control_status` TEXT,
	`is_active` INTEGER DEFAULT 1,
	`is_deleted` INTEGER DEFAULT 0,
	`is_sent` INTEGER DEFAULT 0
);
INSERT INTO temp_customers SELECT * FROM customers;
-- instead drop rename to customers_backup for backup
-- delete in next version
-- DROP TABLE customers;
ALTER TABLE customers RENAME TO customers_backup;
ALTER TABLE temp_customers RENAME TO customers;

-- delete dummy potential customers
delete from customers where customer_no is null or customer_no like '';