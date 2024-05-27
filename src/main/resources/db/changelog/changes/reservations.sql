CREATE TABLE `reservations`
(
    `id`           BIGINT AUTO_INCREMENT PRIMARY KEY,
    `location_id`  BIGINT,
    `volunteer_id` BIGINT,
    `start_time`   TIME,
    `end_time`     TIME,
    `created_at`    DATE,
    `status`       BOOLEAN,
    FOREIGN KEY (`volunteer_id`) REFERENCES `volunteers` (`id`),
    FOREIGN KEY (`location_id`) REFERENCES `locations` (`id`)
);