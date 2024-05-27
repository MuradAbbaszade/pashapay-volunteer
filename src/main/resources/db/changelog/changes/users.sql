CREATE TABLE `users` (
                        `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
                        `username` varchar(255) NOT NULL ,
                        `password` varchar(255) NOT NULL ,
                        `is_enabled` boolean NOT NULL
);

