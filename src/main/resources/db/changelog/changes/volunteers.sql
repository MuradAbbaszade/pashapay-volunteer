CREATE TABLE `volunteers` (
                              `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
                              `user_id` BIGINT,
                              `name` VARCHAR(100),
                              `surname` VARCHAR(100),
                              `created_at` DATETIME,
                              `fin_code` varchar(7),
                              `phone_number` varchar(50),
                              `date_of_birth` DATE,
                              `date_of_employment` DATETIME,
                              `date_of_resignation` DATETIME,
                              `university` VARCHAR(100),
                              `address` VARCHAR(100),
                              `form_status` BOOL,
                              FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
);