package by.vorakh.dev.fibo.redis

import by.vorakh.dev.fibo.redis.entity.ProcessingTime
import by.vorakh.dev.fibo.redis.repository.ProcessingTimeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
class RedisIntegrationTest extends Specification {

    @Autowired
    ProcessingTimeRepository repository

    def "add a processing time"() {

        given:
            def taskId = 1L
            def time = 64300L
            def processingTime = new ProcessingTime(taskId, time)
            repository.add(processingTime).join()
        when:
            def result = repository.findProcessingTime(taskId).join()
        then:
            result != null
            result == time
    }
}