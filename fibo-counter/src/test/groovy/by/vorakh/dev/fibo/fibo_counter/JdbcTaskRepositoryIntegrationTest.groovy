package by.vorakh.dev.fibo.fibo_counter

import by.vorakh.dev.fibo.fibo_counter.TestApplication
import by.vorakh.dev.fibo.fibo_counter.repository.TaskRepository
import by.vorakh.dev.fibo.fibo_counter.repository.entity.TaskEntity
import org.flywaydb.core.Flyway
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

import java.sql.Timestamp

@SpringBootTest(classes = TestApplication)
class JdbcTaskRepositoryIntegrationTest extends Specification {

    @Autowired
    TaskRepository taskRepository
    @Autowired
    Flyway flyway

    void setup() {

        flyway.migrate()
    }

    void cleanup() {

        flyway.clean()
    }

    def "get a task by id if the task was created in a database"() {

        given:
            def id = 1
        when:
            def result = taskRepository.getBy(id).join()
        then:
            result != null
            result.id == id
            result.number == 2
            result.completed == true
            result.startProcessing.toString() == "2020-06-22 13:08:13.0"
            result.finishProcessing.toString() == "2020-06-22 13:09:00.0"
    }

    def "get a task entity with id if the task creates in a database"() {

        given:
            def number = 3
            def startTaskTime = new Timestamp(System.currentTimeMillis())
        when:
            def result = taskRepository.create(new TaskEntity(number, startTaskTime)).join()
        then:
            result != null
            result.number == number
            result.id > 0L
    }

    def "get null if task is not reated in a database"() {

        given:
            def id = 10
        when:
            def result = taskRepository.getBy(id).join()
        then:
            result == null
    }

    def "get an updated task if the task was created in a database"() {

        given:
            def id = 2
            def finishTaskTime = Timestamp.valueOf("2020-06-22 13:09:00.0")
        when:
            taskRepository.update(id, finishTaskTime).join()
            def result = taskRepository.getBy(id).join()
        then:
            result != null
            result.id == id
            result.number == 200
            result.completed == true
            result.startProcessing.toString() == "2020-06-22 13:02:13.0"
            result.finishProcessing.toString() == "2020-06-22 13:09:00.0"
    }
}
