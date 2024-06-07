CREATE TABLE `team_leaders` (
                              `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
                              `user_id` BIGINT,
                              `name` VARCHAR(100),
                              `surname` VARCHAR(100),
                              `phone_number` varchar(50),
                              `created_at` DATETIME,
                              FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
);