
ALTER TABLE `sale_orders` ADD `requested_delivery_date` TEXT DEFAULT NULL;
ALTER TABLE `sale_order_lines` ADD `location_code` TEXT DEFAULT '001';
ALTER TABLE `service_orders` ADD `address_no` TEXT DEFAULT NULL;