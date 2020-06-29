package by.vorakh.dev.fibo.receiver

import by.vorakh.dev.fibo.Application
import by.vorakh.dev.fibo.receiver.model.payload.SequenceSize
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.core.env.Environment
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.test.context.jdbc.Sql
import spock.lang.Shared
import spock.lang.Specification

import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application)
@Sql(executionPhase = BEFORE_TEST_METHOD, scripts = ["/set_autoincrement.sql"])
@Sql(executionPhase = AFTER_TEST_METHOD, scripts = ["/clean_up.sql"])
class TaskIntegrationTest extends Specification {

    @LocalServerPort
    private int port

    @Autowired
    TestRestTemplate client

    @Autowired
    Environment environment

    @Shared
        driverClassName
    @Shared
        url
    @Shared
        username
    @Shared
        password

    void setup() {

        if (driverClassName == null) {
            driverClassName = environment.getProperty("dataSource.driverClassName");
            url = environment.getProperty("dataSource.url");
            username = environment.getProperty("dataSource.username");
            password = environment.getProperty("dataSource.password");
        }
    }

    def cleanupSpec() {

        def connection = groovy.sql.Sql.newInstance(url, username, password, driverClassName)
        connection.execute("DROP TABLE tasks")
        connection.execute("DROP TABLE flyway_schema_history")
        println("Cleanup database after all tests!")
        connection.close()
    }

    def "get a bad request when a task is created  with incorrect data"() {

        given:
            def sequenceSize = new SequenceSize(0)
            def headers = new HttpHeaders()
            def request = new HttpEntity<SequenceSize>(sequenceSize, headers)
            def url = "http://localhost:" + port + "/task"
        when:
            def response = client.exchange(url, HttpMethod.POST, request, String.class)
        then:
            response != null
            response.statusCode.value() == 400
            response.body == "{\"message\":\"N should be less than or equals 2000 and be greater than 0\"}"
    }

    def "get a response for a created task if the created task with correct data"() {

        given:
            def sequenceSize = new SequenceSize(2)
            def headers = new HttpHeaders()
            def request = new HttpEntity<SequenceSize>(sequenceSize, headers)
            def url = "http://localhost:" + port + "/task"
        when:
            def response = client.exchange(url, HttpMethod.POST, request, String.class)
            Thread.sleep(3000L)
        then:
            response != null
            response.statusCode.value() == 200
            response.body.contains("\"id\":1,\"creationTime\":") == true
    }
}