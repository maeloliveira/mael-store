--CREATE TABLE `customer` (
--  `id` int NOT NULL AUTO_INCREMENT,
--  `email` varchar(255) DEFAULT NULL,
--  `name` varchar(255) DEFAULT NULL,
--  `status` enum('ATIVO','INATIVO') DEFAULT NULL,
--  `password` varchar(255) DEFAULT NULL,
--  PRIMARY KEY (`id`)
--);
--  ##Use that follow field to update table
    ALTER TABLE customer ADD COLUMN password varchar(255);