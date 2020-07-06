package by.vorakh.dev.fibo.redisson.repository.impl

import by.vorakh.dev.fibo.base.entity.ProcessingTime
import by.vorakh.dev.fibo.redisson.repository.ReactiveProcessingTimeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
class RedissonIntegrationTest extends Specification {

    @Autowired
    ReactiveProcessingTimeRepository repository

    def "get True when a processing time is added in Redis"() {

        given:
            def processingTime = new ProcessingTime(25L, 200024L)
        when:
            def result = repository.add(processingTime).block()
        then:
            result == true
    }

    def "get nothing if a processing time is not contained in Redis"() {

        given:
            def taskId = 2452529757338L
        when:
            def result = repository.getBy(taskId).block()
        then:
            result == null
    }

    def "get a processing time if the processing time is not contained in Redis"() {

        given:
            def taskId = 24L
            def time = 200024L
            def processingTime = new ProcessingTime(taskId, time)
            repository.add(processingTime).block()
        when:
            def result = repository.getBy(taskId).toFuture().join()
        then:
            result != null
            result.id == taskId
            result.time == time
    }
}