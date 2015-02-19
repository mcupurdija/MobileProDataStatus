ALTER TABLE `sale_order_lines` ADD `vat_rate` REAL;

UPDATE `app_settings` SET app_version='1.2.0', app_sync_warnning_date=datetime('now') WHERE _id=1;