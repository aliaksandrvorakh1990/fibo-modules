package by.vorakh.dev.fibo.fibo_counter

import by.vorakh.dev.fibo.fibo_counter.TestApplication
import by.vorakh.dev.fibo.fibo_counter.repository.TaskResultRepository
import by.vorakh.dev.fibo.fibo_counter.repository.entity.TaskResultEntity
import org.flywaydb.core.Flyway
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest(classes = TestApplication)
class JdbcTaskResultRepositoryIntegrationTest extends Specification {

    @Autowired
    TaskResultRepository taskResultRepository

    @Autowired
    Flyway flyway

    void setup() {

        flyway.migrate()
    }

    void cleanup() {

        flyway.clean()
    }

    def "get a task result if the task result is created in DB"() {

        given:
            def taskId = 2L
            def result = "0, 1"
            def taskResult = new TaskResultEntity(taskId, result)
        when:
            def actual = taskResultRepository.create(taskResult).join()
        then:
            actual != null
            actual.id == taskId
            actual.result == result
    }

    def "get an existed result"() {

        given:
            def taskId = 1L
        when:
            def actual = taskResultRepository.getResultBy(taskId).join()
        then:
            actual != null
            actual == "0, 1"
    }

    def "get null when the task result is not exist"() {

        given:
            def taskId = 10L
        when:
            def actual = taskResultRepository.getResultBy(taskId).join()
        then:
            actual == null
    }
}