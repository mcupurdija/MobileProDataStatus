-- service orders changes

ALTER TABLE `service_orders` ADD `sales_person_id` INTEGER DEFAULT NULL;
ALTER TABLE `service_orders` ADD `reclamation_description` TEXT DEFAULT NULL;

-- sent orders changes

ALTER TABLE `sent_orders_status_lines` ADD `reserved` REAL DEFAULT 0;