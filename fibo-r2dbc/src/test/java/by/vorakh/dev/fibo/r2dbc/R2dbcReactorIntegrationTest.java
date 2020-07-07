package by.vorakh.dev.fibo.r2dbc;

import by.vorakh.dev.fibo.base.entity.TaskEntity;
import by.vorakh.dev.fibo.base.entity.TaskStatus;
import by.vorakh.dev.fibo.r2dbc.repository.TaskRepository;
import lombok.extern.java.Log;
import org.flywaydb.core.Flyway;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@Log
@RunWith(SpringRunner.class)
@SpringBootTest
@Sql(executionPhase = BEFORE_TEST_METHOD, scripts = {"/set_autoincrement.sql", "/insert_test_data.sql"})
@Sql(executionPhase = AFTER_TEST_METHOD, scripts = {"/clean_up.sql"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class R2dbcReactorIntegrationTest {

    @Autowired
    private TaskRepository repository;

    @Autowired
    private ApplicationContext context;

    private static Flyway flyway;

    @Before
    public void setUp() throws Exception {

        Hooks.onOperatorDebug();
        if (flyway == null) {
            flyway = context.getBean("flyway", Flyway.class);
        }
    }

    @AfterClass
    public static void afterClass() throws Exception {

        flyway.clean();
    }

    @Test
    public void createTaskEntity() {

        long creationTime = System.currentTimeMillis();
        TaskEntity task = new TaskEntity(3, creationTime);
        TaskEntity expectTask = new TaskEntity(5L, 3, TaskStatus.CREATED, creationTime, 0, null);

        Mono<TaskEntity> actual = repository.create(task);

        StepVerifier.create(actual).expectNext(expectTask).expectComplete().verify();
    }

    @Test
    public void getAll() {

        TaskEntity firstTask = new TaskEntity(1, 2, TaskStatus.COMPLETED, 1593024253765L, 1593025749413L, "0, 1");
        TaskEntity secondTask = new TaskEntity(2, 4, TaskStatus.PROCESSING, 1593024256765L, 0L, null);
        TaskEntity thirdTask = new TaskEntity(3, 5, TaskStatus.FAILED, 1593024753765L, 0L, null);
        TaskEntity fourthTask = new TaskEntity(4, 15, TaskStatus.CREATED, 1593024953765L, 0L, null);

        Flux<TaskEntity> tasks = repository.getAll();

        StepVerifier.create(tasks).expectNext(firstTask, secondTask, thirdTask, fourthTask).expectComplete().verify();
    }

    @Test
    public void getExistedTask() {

        long taskId = 1L;

        TaskEntity expected = new TaskEntity(1, 2, TaskStatus.COMPLETED, 1593024253765L, 1593025749413L, "0, 1");

        Mono<TaskEntity> actual = repository.getBy(taskId);

        StepVerifier.create(actual).expectNext(expected).expectComplete().verify();
    }

    @Test
    public void getNotExistedTask() {

        long taskId = 10L;

        Mono<TaskEntity> actual = repository.getBy(taskId);

        StepVerifier.create(actual).verifyComplete();
    }

    @Test
    public void deleteExistedTaskById() {

        long taskId = 2;

        Mono<Integer> actual = repository.delete(taskId);

        Integer expected = 1;

        StepVerifier.create(actual).expectNext(expected).expectComplete().verify();
    }

    @Test
    public void deleteNotExistedTaskById() {

        long taskId = 20;

        Mono<Integer> actual = repository.delete(taskId);

        Integer expected = 0;

        StepVerifier.create(actual).expectNext(expected).expectComplete().verify();
    }

    @Test
    public void updateExistedTaskBy_Id_And_Status() {

        long taskId = 4;
        TaskStatus processingStatus = TaskStatus.PROCESSING;

        Mono<Integer> actual = repository.update(taskId, processingStatus);

        Integer expected = 1;

        StepVerifier.create(actual).expectNext(expected).expectComplete().verify();
    }

    @Test
    public void updateNotExistedTaskBy_Id_And_Status() {

        long taskId = 20;
        TaskStatus processingStatus = TaskStatus.PROCESSING;

        Mono<Integer> actual = repository.update(taskId, processingStatus);

        Integer expected = 0;

        StepVerifier.create(actual).expectNext(expected).expectComplete().verify();
    }

    @Test
    public void updateExistedTaskBy_Id_EndTime_Status_Result() {

        long taskId = 4;
        long endTime = System.currentTimeMillis();
        TaskStatus processingStatus = TaskStatus.PROCESSING;
        String result = "0, 1, 1, 2";

        Mono<Integer> actual = repository.update(taskId, endTime, processingStatus, result);

        Integer expected = 1;

        StepVerifier.create(actual).expectNext(expected).expectComplete().verify();
    }

    @Test
    public void updateNotExistedTaskBy_Id_EndTime_Status_Result() {

        long taskId = 20;
        long endTime = System.currentTimeMillis();
        TaskStatus processingStatus = TaskStatus.PROCESSING;
        String result = "0, 1, 1, 2";

        Mono<Integer> actual =  repository.update(taskId, endTime, processingStatus, result);

        Integer expected = 0;

        StepVerifier.create(actual).expectNext(expected).expectComplete().verify();
    }
}