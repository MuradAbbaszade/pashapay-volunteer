CREATE TABLE `user_authorities` (
                        `authority_id` BIGINT NOT NULL ,
                        `user_id` BIGINT NOT NULL ,
                        FOREIGN KEY (`authority_id`) REFERENCES `authorities` (`id`),
                        FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
);