package by.vorakh.dev.fibo.r2dbc

import by.vorakh.dev.fibo.base.entity.TaskEntity
import by.vorakh.dev.fibo.base.entity.TaskStatus
import by.vorakh.dev.fibo.r2dbc.repository.TaskRepository
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
class R2dbcReadOperationIntegrationTest extends Specification {

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

    def "get a task list "() {

        when:
            def actualList = repository.getAll().collectList().block()
        then:
            actualList != null
            actualList.size() == 4
    }

    def "have #task received by selecting by #id"() {

        expect:
            repository.getBy(id).block() == task
        where:
            id || task
            1  || new TaskEntity(1, 2, TaskStatus.COMPLETED, 1593024253765L, 1593025749413L, "0, 1")
            2  || new TaskEntity(2, 4, TaskStatus.PROCESSING, 1593024256765L, 0L, null)
            3  || new TaskEntity(3, 5, TaskStatus.FAILED, 1593024753765L, 0L, null)
            4  || new TaskEntity(4, 15, TaskStatus.CREATED, 1593024953765L, 0L, null)
            10 || null
    }
}