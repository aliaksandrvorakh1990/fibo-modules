CREATE TABLE IF NOT EXISTS `tasks` (
    `task_id` BIGINT AUTO_INCREMENT,
    `number` INT NOT NULL,
    `status` VARCHAR (15) NOT NULL,
    `startProcessing` BIGINT NOT NULL,
    `finishProcessing` BIGINT,
    `result` TEXT,
    CONSTRAINT task_pk PRIMARY KEY (task_id)
);
