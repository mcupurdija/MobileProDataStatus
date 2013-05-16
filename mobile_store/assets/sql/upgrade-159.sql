-- upgrade older 158 version to 159
-- date 16.5.2013.

ALTER TABLE `customers` ADD `apr_customer_turnover` REAL DEFAULT 0;

UPDATE `customers` SET is_active = 1 WHERE contact_company_no is null or contact_company_no = '';