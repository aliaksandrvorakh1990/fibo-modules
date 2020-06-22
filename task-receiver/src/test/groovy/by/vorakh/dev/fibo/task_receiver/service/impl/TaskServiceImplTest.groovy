package by.vorakh.dev.fibo.task_receiver.service.impl

import by.vorakh.dev.fibo.fibo_counter.repository.TaskRepository
import by.vorakh.dev.fibo.fibo_counter.repository.entity.TaskEntity
import by.vorakh.dev.fibo.fibo_counter.service.TaskSolvingService
import by.vorakh.dev.fibo.task_receiver.exception.NoCompletedTaskException
import by.vorakh.dev.fibo.task_receiver.exception.NoExistTaskException
import spock.lang.Specification

import java.sql.Timestamp
import java.util.concurrent.CompletionException

import static java.util.concurrent.CompletableFuture.completedFuture

class TaskServiceImplTest extends Specification {

    def taskRepository = Mock(TaskRepository.class)
    def taskSolvingService = Mock(TaskSolvingService.class)
    def taskService = new TaskServiceImpl(taskRepository, taskSolvingService)

    def "getting result if task is solved"() {

        given:
            def taskId = 2L;
        and:
            def currentTimeMillis = System.currentTimeMillis()
            def startTime = new Timestamp(currentTimeMillis)
            def fiveSeconds = 5000L
            def finishTime = new Timestamp(currentTimeMillis + fiveSeconds)
            def solvedTask = new TaskEntity(taskId, 2, true, startTime, finishTime)
            def fiboResult = "0, 1"
        when:
            def result = taskService.getTask(taskId)
        then:
            1 * taskRepository.getBy({ it == taskId }) >> completedFuture(solvedTask)
            1 * taskSolvingService.getResult({ it == taskId }) >> completedFuture(fiboResult)
        then:
            result.get().result == fiboResult
    }

    def "thrown NoExistTaskException if task is not exist"() {

        given:
            def taskId = 2L;
        when:
            taskService.getTask(taskId).join()
        then:
            1 * taskRepository.getBy({ it == taskId }) >> completedFuture(null)
            1 * taskSolvingService.getResult({ it == taskId }) >> completedFuture(null)
        then:
            def exception = thrown(CompletionException)
            exception.getCause() instanceof NoExistTaskException
    }

    def "thrown NoCompletedTaskException if task is not Completed"() {

        given:
            def taskId = 2L;
        and:
            def currentTimeMillis = System.currentTimeMillis()
            def startTime = new Timestamp(currentTimeMillis)
            def fiveSeconds = 5000L
            def finishTime = new Timestamp(currentTimeMillis + fiveSeconds)
            def noCompletedTask = new TaskEntity(taskId, 2, false, startTime, finishTime)
        when:
            taskService.getTask(taskId).join()
        then:
            1 * taskRepository.getBy({ it == taskId }) >> completedFuture(noCompletedTask)
            1 * taskSolvingService.getResult({ it == taskId }) >> completedFuture(null)
        then:
            def exception = thrown(CompletionException)
            exception.getCause() instanceof NoCompletedTaskException
    }
}
