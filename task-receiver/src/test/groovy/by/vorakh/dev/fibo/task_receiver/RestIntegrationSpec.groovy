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
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import spock.lang.Specification

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application)
class RestIntegrationSpec extends Specification {

    @LocalServerPort
    private int port

    @Autowired
    ApplicationContext context
    @Autowired
    private TestRestTemplate restTemplate

    def "get task if task is exist and completed"() {

        given:
            def url = "http://localhost:" + port + "/task/1"

        when:
            def response = this.restTemplate.exchange(url, HttpMethod.GET, null, SolvedTaskViewModel.class)
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
            def response = this.restTemplate.exchange(url, HttpMethod.GET, null, String.class)
        then:
            response != null
            response.statusCode.value() == 204
    }

    def "get not found if task is not exist"() {

        given:
            def url = "http://localhost:" + port + "/task/10";
        when:
            def response = this.restTemplate.exchange(url, HttpMethod.GET, null, String.class)
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
            def response = this.restTemplate.exchange(url, HttpMethod.POST, request, String.class)
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
            def response = this.restTemplate.exchange(url, HttpMethod.POST, request, SuccessCreatingTaskResponse.class)
        then:
            response != null
            response.statusCode.value() == 200
            println(response.body)
            response.body.taskId >= 3
            response.body.accepted == true
            response.body.timestamp != null
    }

    @TestConfiguration
    @Import(AppConfiguration)
    static class TestConfig {
    }
}