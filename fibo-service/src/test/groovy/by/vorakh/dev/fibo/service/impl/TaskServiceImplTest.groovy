package by.vorakh.dev.fibo.service.impl

import by.vorakh.dev.fibo.base.entity.TaskEntity
import by.vorakh.dev.fibo.base.entity.TaskStatus
import by.vorakh.dev.fibo.base.exception.NoCompletedTaskException
import by.vorakh.dev.fibo.base.exception.NoExistTaskException
import by.vorakh.dev.fibo.base.model.SequenceSize
import by.vorakh.dev.fibo.jdbc.repository.TaskRepository
import by.vorakh.dev.fibo.redis.repository.ProcessingTimeRepository
import by.vorakh.dev.fibo.service.impl.TaskServiceImpl
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import spock.lang.Specification

import java.util.concurrent.CompletionException

import static java.util.concurrent.CompletableFuture.completedFuture

class TaskServiceImplTest extends Specification {

    def taskRepository = Mock(TaskRepository)
    def executor = {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor()
        executor.setCorePoolSize(2)
        executor.setMaxPoolSize(5)
        executor.setQueueCapacity(500)
        executor.initialize()
        return executor
    }
    def processingTimeRepository = Mock(ProcessingTimeRepository)
    def taskService = new TaskServiceImpl(taskRepository, executor, processingTimeRepository)

    def "get a creation response if the task is created and is processing"() {

        given:
            def number = 2
            def sequenceSize = new SequenceSize(number)
        and:
            def startTime = System.currentTimeMillis()
            def status = TaskStatus.CREATED
            def id = 2
            def newTask = new TaskEntity(id, number, status, startTime, 0L, null)
        when:
            def result = taskService.createTask(sequenceSize).join()
        then:
            1 * taskRepository.create({ it.number == number }) >> completedFuture(newTask)
            1 * taskRepository.update({ it == id }, { it == TaskStatus.PROCESSING }) >> completedFuture(null)
        then:
            result != null
    }

    def "get a result if the task is solved"() {

        given:
            def taskId = 2L
        and:
            def currentTimeMillis = System.currentTimeMillis()
            def startTime = System.currentTimeMillis()
            def fiveSeconds = 5000L
            def finishTime = currentTimeMillis + fiveSeconds
            def fiboResult = "0, 1"
            def solvedTask = new TaskEntity(taskId, 2, TaskStatus.COMPLETED, startTime, finishTime, fiboResult)
        when:
            def result = taskService.getTaskResult(taskId)
        then:
            1 * taskRepository.getBy({ it == taskId }) >> completedFuture(solvedTask)
            1 * processingTimeRepository.findProcessingTime(taskId) >> completedFuture(fiveSeconds)
        then:
            result != null
            result.get().result == fiboResult
            result.get().status == TaskStatus.COMPLETED
            result.get().processingTime != null
    }

    def "thrown NoExistTaskException if a task is not exist"() {

        given:
            def taskId = 34L
        when:
            taskService.getTaskResult(taskId).join()
        then:
            1 * taskRepository.getBy({ it == taskId }) >> completedFuture(null)
            1 * processingTimeRepository.findProcessingTime(taskId) >> completedFuture(null)
        then:
            def exception = thrown(CompletionException)
            exception.getCause() instanceof NoExistTaskException
    }

    def "thrown NoCompletedTaskException if the task status is processing"() {

        given:
            def taskId = 5L
        and:
            def startTime = System.currentTimeMillis()
            def processingTask = new TaskEntity(taskId, 2, TaskStatus.PROCESSING, startTime, 0L, null)
        when:
            taskService.getTaskResult(taskId).join()
        then:
            1 * taskRepository.getBy({ it == taskId }) >> completedFuture(processingTask)
            1 * processingTimeRepository.findProcessingTime(taskId) >> completedFuture(null)
        then:
            def exception = thrown(CompletionException)
            exception.getCause() instanceof NoCompletedTaskException
    }
}
