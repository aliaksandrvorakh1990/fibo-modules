
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

DELETE FROM task_results;
DELETE FROM tasks;

INSERT INTO tasks(`task_id`, `number`, `isCompleted`, `startProcessing`, `finishProcessing`)
VALUES (1, 2, TRUE, '2020-06-22 13:08:13', '2020-06-22 13:09:00');

INSERT INTO task_results(task_result_id, result) VALUES (1, '0, 1');

INSERT INTO tasks(`task_id`, `number`, `isCompleted`, `startProcessing`)
VALUES (2, 200, FALSE , '2020-06-22 13:02:13');

