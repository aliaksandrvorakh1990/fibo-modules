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
import spock.lang.Shared
import spock.lang.Specification

import javax.sql.DataSource

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application)
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
            def size = 2
            def sequenceSize = new SequenceSize(size)
            def headers = new HttpHeaders()
            def request = new HttpEntity<SequenceSize>(sequenceSize, headers)
            def url = "http://localhost:" + port + "/task"
        and:
            def currentTimeMillis = System.currentTimeMillis()
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
        and:
            def id = body.path("id").asLong()
        when:
            def result = connection.firstRow('SELECT task_id, number, status, creationTime, endTime, result FROM tasks WHERE task_id = ?', id)
        then:
            result != null
            result.get("task_id") == id
            result.get("number") == size
            result.get("status") != null
            result.get("creationTime") > currentTimeMillis
            result.get("endTime") > currentTimeMillis
            result.get("result") != null
            result.get("result") == "0, 1"
    }
}