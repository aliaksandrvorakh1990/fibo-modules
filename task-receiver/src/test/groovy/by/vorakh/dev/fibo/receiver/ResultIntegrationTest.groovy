package by.vorakh.dev.fibo.receiver

import by.vorakh.dev.fibo.Application
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.core.env.Environment
import org.springframework.http.HttpMethod
import org.springframework.test.context.jdbc.Sql
import spock.lang.Shared
import spock.lang.Specification

import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application)
@Sql(executionPhase = BEFORE_TEST_METHOD, scripts = ["/set_autoincrement.sql", "/insert_test_data.sql"])
@Sql(executionPhase = AFTER_TEST_METHOD, scripts = ["/clean_up.sql"])
class ResultIntegrationTest extends Specification {

    @LocalServerPort
    private int port

    @Autowired
    TestRestTemplate client

    @Autowired
    final ObjectMapper objectMapper

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

    def "get a result response with the completed status if the task is existed and completed"() {

        given:
            def url = "http://localhost:" + port + "/result/1"
        when:
            def response = client.exchange(url, HttpMethod.GET, null, String.class)
        then:
            response != null
            response.statusCode.value() == 200

        when:
            def body = objectMapper.readTree(response.body)
        then:
            body.path("status").asText() != null
            body.path("status").asText() == "COMPLETED"
            body.path("result").asText() != null
            body.path("result").asText() == "0, 1"
    }

    def "get a result response with the failed status and a null result if the task was failed"() {

        given:
            def url = "http://localhost:" + port + "/result/3"
        when:
            def response = client.exchange(url, HttpMethod.GET, null, String.class)
        then:
            response != null
            response.statusCode.value() == 200
        when:
            def body = objectMapper.readTree(response.body)
        then:
            body.path("status").asText() != null
            body.path("status").asText() == "FAILED"
            body.path("result").isNull() == true
    }

    def "get no content if a task has the CREATED status in the DB"() {

        given:
            def url = "http://localhost:" + port + "/result/4"
        when:
            def response = client.exchange(url, HttpMethod.GET, null, String.class)
        then:
            response != null
            response.statusCode.value() == 204
    }

    def "get no content if a task has the PROCESSING status in the DB"() {

        given:
            def url = "http://localhost:" + port + "/result/2";
        when:
            def response = client.exchange(url, HttpMethod.GET, null, String.class)
        then:
            response != null
            response.statusCode.value() == 204
    }

    def "get not found if a task is not exist"() {

        given:
            def url = "http://localhost:" + port + "/result/10";
        when:
            def response = client.exchange(url, HttpMethod.GET, null, String.class)
        then:
            response != null
            response.statusCode.value() == 404
            response.body == "{\"message\":\"Task with this id is not exist.\"}"
    }
}