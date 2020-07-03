package by.vorakh.dev.fibo.jdbc

import by.vorakh.dev.fibo.base.entity.TaskEntity
import by.vorakh.dev.fibo.base.entity.TaskStatus
import by.vorakh.dev.fibo.jdbc.repository.TaskRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.jdbc.Sql
import spock.lang.Shared
import spock.lang.Specification

import javax.sql.DataSource

import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD

@SpringBootTest
@Sql(executionPhase = BEFORE_TEST_METHOD, scripts = ["/set_autoincrement.sql", "/insert_test_data.sql"])
@Sql(executionPhase = AFTER_TEST_METHOD, scripts = ["/clean_up.sql"])
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class JdbcIntegrationTest extends Specification {

    @Autowired
    TaskRepository taskRepository

    @Autowired
    DataSource dataSource

    @Shared
        connection

    void setup() {

        if (connection == null) {
            connection = new groovy.sql.Sql(dataSource)
        }
    }

    def cleanupSpec() {

        connection.execute("DROP TABLE tasks")
        connection.execute("DROP TABLE flyway_schema_history")
        println("Cleanup database after all tests!")
        connection.close()
    }

    def "get a task by #id is #task"() {

        expect:
            taskRepository.getBy(id).join() == task
        where:
            id || task
            1  || new TaskEntity(1, 2, TaskStatus.COMPLETED, 1593024253765L, 1593025749413L, "0, 1")
            2  || new TaskEntity(2, 4, TaskStatus.PROCESSING, 1593024256765L, 0L, null)
            3  || new TaskEntity(3, 5, TaskStatus.FAILED, 1593024753765L, 0L, null)
            4  || new TaskEntity(4, 15, TaskStatus.CREATED, 1593024953765L, 0L, null)
            10 || null
    }

    def "get a created task if the new task is created"() {

        given:
            def newTask = new TaskEntity(2, 1593024953765L)
        when:
            def result = taskRepository.create(newTask).join()
        then:
            result != null
            result == new TaskEntity(5, 2, TaskStatus.CREATED, 1593024953765L, 0L, null)
    }

    def "update a task status by id and a new status"() {

        given:
            def taskId = 4
            def futureStatus = TaskStatus.PROCESSING
        when:
            taskRepository.update(taskId, futureStatus).join()
            def result = taskRepository.getBy(taskId).join()
        then:
            result == new TaskEntity(4, 15, TaskStatus.PROCESSING, 1593024953765L, 0L, null)
    }

    def "update a task when the task is completed"() {

        given:
            def taskId = 2
            def completedStatus = TaskStatus.COMPLETED
            def result = "0, 1, 1, 2"
            def endTime = 1593024993765L
        when:
            taskRepository.update(taskId, endTime, completedStatus, result).join()
            def actual = taskRepository.getBy(taskId).join()
        then:
            actual == new TaskEntity(taskId, 4, completedStatus, 1593024256765L, endTime, result)
    }
}
