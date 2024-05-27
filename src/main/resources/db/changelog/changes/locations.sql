CREATE TABLE `locations` (
                           `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
                           `description` varchar(200),
                           `target` varchar(200),
                           `district` varchar(200),
                           `market` varchar(200),
                           `subway` varchar(200),
                           `capacity` integer
);