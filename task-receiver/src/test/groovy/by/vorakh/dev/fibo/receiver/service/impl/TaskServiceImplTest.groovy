package by.vorakh.dev.fibo.receiver.service.impl

import by.vorakh.dev.fibo.counter.repository.TaskRepository
import by.vorakh.dev.fibo.counter.repository.entity.TaskEntity
import by.vorakh.dev.fibo.counter.repository.entity.TaskStatus
import by.vorakh.dev.fibo.receiver.exception.NoCompletedTaskException
import by.vorakh.dev.fibo.receiver.exception.NoExistTaskException
import by.vorakh.dev.fibo.receiver.model.payload.SequenceSize
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import spock.lang.Specification

import java.util.concurrent.CompletionException

import static java.util.concurrent.CompletableFuture.completedFuture

class TaskServiceImplTest extends Specification {

    def taskRepository = Mock(TaskRepository)
    def executor = {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(500);
        executor.initialize();
        return executor;
    }
    def taskService = new TaskServiceImpl(taskRepository, executor)

    def "get created task if task is created and is processing"() {

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

    def "getting result if task is solved"() {

        given:
            def taskId = 2L;
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
        then:
            result != null
            result.get().result == fiboResult
            result.get().status == TaskStatus.COMPLETED
    }

    def "thrown NoExistTaskException if task is not exist"() {

        given:
            def taskId = 34L;
        when:
            taskService.getTaskResult(taskId).join()
        then:
            1 * taskRepository.getBy({ it == taskId }) >> completedFuture(null)
        then:
            def exception = thrown(CompletionException)
            exception.getCause() instanceof NoExistTaskException
    }

    def "thrown NoCompletedTaskException if task status is processing"() {

        given:
            def taskId = 5L;
        and:
            def startTime = System.currentTimeMillis()
            def processingTask = new TaskEntity(taskId, 2, TaskStatus.PROCESSING, startTime, 0L, null)
        when:
            taskService.getTaskResult(taskId).join()
        then:
            1 * taskRepository.getBy({ it == taskId }) >> completedFuture(processingTask)
        then:
            def exception = thrown(CompletionException)
            exception.getCause() instanceof NoCompletedTaskException
    }
}
