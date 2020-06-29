package by.vorakh.dev.fibo.receiver

import by.vorakh.dev.fibo.Application
import by.vorakh.dev.fibo.receiver.model.payload.SequenceSize
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.test.context.jdbc.Sql
import spock.lang.Shared
import spock.lang.Specification

import javax.sql.DataSource

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
    final ObjectMapper objectMapper

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
        when:
            def body = objectMapper.readTree(response.body)
        then:
            body.path("message").asText() != null
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
        when:
            def body = objectMapper.readTree(response.body)
        then:
            body.path("id").asLong() == 1
            body.path("creationTime").asText() != null
    }
}