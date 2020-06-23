CREATE TABLE IF NOT EXISTS `tasks` (
    `task_id` BIGINT AUTO_INCREMENT,
    `number` INT NOT NULL,
    `isCompleted` BOOLEAN,
    `startProcessing` TIMESTAMP NOT NULL,
    `finishProcessing` TIMESTAMP,
    CONSTRAINT task_pk PRIMARY KEY (task_id)
);

CREATE TABLE If NOT EXISTS `task_results` (
    `task_result_id` BIGINT UNIQUE NOT NULL ,
    `result` TEXT NOT NULL,
     CONSTRAINT rlt_task_fk FOREIGN KEY (task_result_id) REFERENCES tasks (task_id)
);
