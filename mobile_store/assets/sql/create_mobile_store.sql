CREATE TABLE `USERS` ( 
       `_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
       `username` TEXT,
       `pass` TEXT,
       `sales_person_id` INTEGER,
       `last_login` TEXT
);
INSERT INTO `USERS` (username,pass,sales_person_id, last_login) VALUES ('tica', 'tica', 1, 'yesterday'); 