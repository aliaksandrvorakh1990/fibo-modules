package by.vorakh.dev.fibo.r2dbc

import by.vorakh.dev.fibo.base.entity.TaskEntity
import by.vorakh.dev.fibo.base.entity.TaskStatus
import by.vorakh.dev.fibo.r2dbc.repository.TaskRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import spock.lang.Shared
import spock.lang.Specification

import javax.sql.DataSource

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class R2dbcWriteOperationIntegrationTest extends Specification {

    @Autowired
    TaskRepository repository
    @Autowired
    DataSource dataSource

    @Shared
    groovy.sql.Sql connection

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

    def "Have a task received if the task is created in DB"() {

        given:

            def number = 3
            def creationTime = System.currentTimeMillis()
            def task = new TaskEntity(3, creationTime)
            def expectTask = new TaskEntity(1L, 3, TaskStatus.CREATED, creationTime, 0, null)
        when:
            def actualTask = repository.create(task).block()
        then:
            actualTask == expectTask
        and:
            def id = actualTask.getId()
        when:
            def result = connection.firstRow('SELECT task_id, number, status, creationTime, endTime, result FROM tasks WHERE task_id = ?', id)
        then:
            result != null
            result.get("task_id") == id
            result.get("number") == number
            result.get("status") != null
            result.get("creationTime") == creationTime
            result.get("endTime") == null
            result.get("result") == null
    }

    def "Have an amount(1) of updated rows received if a task status is updated by the task id in the database"() {

        given:
            def id = 1
            def newStatus = TaskStatus.FAILED
        when:
            def actual = repository.update(id, newStatus).block()
        then:
            actual == 1
            def result = connection.firstRow('SELECT status FROM tasks WHERE task_id = ?', id)
        then:
            result != null
            result.get("status") != null
            result.get("status") == newStatus.name()
    }

    def "Have an amount(0) of updated rows received if the task does not exist"() {

        given:
            def notExistId = 10
            def newStatus = TaskStatus.FAILED
        when:
            def actual = repository.update(notExistId, newStatus).block()
        then:
            actual == 0
    }

    def "Have an amount(1) of updated rows received if the 'task status', 'end time' and 'result' fields are updated by the task id in the database"() {

        given:
            def id = 1
            def endTime = System.currentTimeMillis()
            def newStatus = TaskStatus.COMPLETED
            def result = "0, 1, 1"
        when:
            def actual = repository.update(id, endTime, newStatus, result).block()
        then:
            actual == 1
            def taskRow = connection.firstRow('SELECT status, endTime, result FROM tasks WHERE task_id = ?', id)
        then:
            taskRow != null
            taskRow.get("status") != null
            taskRow.get("status") == newStatus.name()
            taskRow.get("endTime") != null
            taskRow.get("endTime") == endTime
            taskRow.get("result") != null
            taskRow.get("result") == result
    }

    def "Have an amount(1) of deleted rows received if the task is deleted a by id"() {

        given:
            def id = 1
        when:
            def actual = repository.delete(id).block()
        then:
            actual == 1
    }


    def "Have an amount(0) of deleted rows received if the task does not exist"() {

        given:
            def id = 10
        when:
            def actual = repository.delete(id).block()
        then:
            actual == 0
    }
}