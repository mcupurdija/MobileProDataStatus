-- upgrade older 156 version to 157
-- date 17.4.2013.

ALTER TABLE `sent_orders_status_lines` ADD `quantity_shipped` REAL DEFAULT 0;
ALTER TABLE `sent_orders_status_lines` ADD `quantity_invoiced` REAL DEFAULT 0;
ALTER TABLE `sent_orders_status_lines` ADD `price_and_disc_are_correct` INTEGER DEFAULT 0;