CREATE TABLE IF NOT EXISTS `tasks` (
    `task_id` BIGINT AUTO_INCREMENT,
    `number` INT NOT NULL,
    `status` ENUM ('CREATED', 'PROCESSING', 'FAILED', 'COMPLETED'),
    `creationTime` BIGINT NOT NULL,
    `endTime` BIGINT,
    `result` TEXT,
    CONSTRAINT task_pk PRIMARY KEY (task_id)
);
