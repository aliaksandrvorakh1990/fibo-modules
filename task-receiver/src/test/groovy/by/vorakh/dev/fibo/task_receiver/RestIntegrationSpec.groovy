package by.vorakh.dev.fibo.task_receiver

import by.vorakh.dev.fibo.Application
import by.vorakh.dev.fibo.task_receiver.configuration.AppConfiguration
import by.vorakh.dev.fibo.task_receiver.model.payload.SequenceSize
import by.vorakh.dev.fibo.task_receiver.model.response.SolvedTaskViewModel
import by.vorakh.dev.fibo.task_receiver.model.response.SuccessCreatingTaskResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Import
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
@Sql(executionPhase = BEFORE_TEST_METHOD, scripts = ["/insert_test_data.sql", "/set_autoincrement.sql"])
@Sql(executionPhase = AFTER_TEST_METHOD, scripts = "/clean_up.sql")
class RestIntegrationSpec extends Specification {

    @LocalServerPort
    private int port

    @Autowired
    ApplicationContext context
    @Autowired
    TestRestTemplate restTemplate

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

        Thread.sleep(2000L)
        driverClassName = environment.getProperty("dataSource.driverClassName");
        url = environment.getProperty("dataSource.url");
        username = environment.getProperty("dataSource.username");
        password = environment.getProperty("dataSource.password");
    }

    def cleanupSpec() {

        def connection = groovy.sql.Sql.newInstance(url, username, password, driverClassName)
        connection.execute("DROP TABLE task_results")
        connection.execute("DROP TABLE tasks")
        connection.execute("DROP TABLE flyway_schema_history")

        println("Cleanup database after all tests!")
    }

    def "get task if task is exist and completed"() {

        given:
            def url = "http://localhost:" + port + "/task/1"

        when:
            def response = restTemplate.exchange(url, HttpMethod.GET, null, SolvedTaskViewModel.class)
        then:
            response != null
            response.statusCode.value() == 200
            println(response.body)
            response.body == new SolvedTaskViewModel("0, 1")
    }

    def "get task if task is exist and not completed"() {

        given:
            def url = "http://localhost:" + port + "/task/2";
        when:
            def response = restTemplate.exchange(url, HttpMethod.GET, null, String.class)
        then:
            response != null
            response.statusCode.value() == 204
    }

    def "get not found if task is not exist"() {

        given:
            def url = "http://localhost:" + port + "/task/10";
        when:
            def response = restTemplate.exchange(url, HttpMethod.GET, null, String.class)
        then:
            response != null
            response.statusCode.value() == 404
            response.body == "Task with this id is not exist."
    }

    def "get a bed request if create task with incorrect data"() {

        given:
            def sequenceSize = new SequenceSize(0)
            def headers = new HttpHeaders()
            def request = new HttpEntity<SequenceSize>(sequenceSize, headers)
            def url = "http://localhost:" + port + "/task"
        when:
            def response = restTemplate.exchange(url, HttpMethod.POST, request, String.class)
        then:
            response != null
            response.statusCode.value() == 400
    }

    def "get a response for a created task if the created task with correct data"() {

        given:
            def sequenceSize = new SequenceSize(2)
            def headers = new HttpHeaders()
            def request = new HttpEntity<SequenceSize>(sequenceSize, headers)
            def url = "http://localhost:" + port + "/task"
        when:
            def response = restTemplate.exchange(url, HttpMethod.POST, request, SuccessCreatingTaskResponse.class)
            Thread.sleep(3000L)
        then:
            response != null
            response.statusCode.value() == 200
            response.body.taskId >= 3
            response.body.accepted == true
            response.body.timestamp != null
    }

    def "get task response after creating, then get no content if task is not solved, get result if task is solved"() {

        given:
            def sequenceSize = new SequenceSize(10)
            def headers = new HttpHeaders()
            def request = new HttpEntity<SequenceSize>(sequenceSize, headers)
            def url = "http://localhost:" + port + "/task"
        when:
            def response = restTemplate.exchange(url, HttpMethod.POST, request, SuccessCreatingTaskResponse.class)
        then:
            response != null
            response.statusCode.value() == 200
            response.body.taskId >= 3
            response.body.accepted == true
            response.body.timestamp != null
            def taskId = response.body.taskId
            def taskUrl = url + "/" + taskId
            def noSolvedTaskResponse = restTemplate.exchange(taskUrl, HttpMethod.GET, null, String.class)
            noSolvedTaskResponse != null
            noSolvedTaskResponse.statusCode.value() == 204
            Thread.sleep(25000L)
            def solvedTaskResponse = restTemplate.exchange(taskUrl, HttpMethod.GET, null, SolvedTaskViewModel.class)
            solvedTaskResponse != null
            solvedTaskResponse.statusCode.value() == 200
            solvedTaskResponse.body != null
            solvedTaskResponse.body.result == "0, 1, 1, 2, 3, 5, 8, 13, 21, 34"
    }

    @TestConfiguration
    @Import(AppConfiguration)
    static class TestConfig {
    }
}