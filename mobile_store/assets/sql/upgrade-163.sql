
ALTER TABLE `sale_orders` ADD `requested_delivery_date` TEXT DEFAULT NULL;
ALTER TABLE `sale_order_lines` ADD `location_code` TEXT DEFAULT '001';