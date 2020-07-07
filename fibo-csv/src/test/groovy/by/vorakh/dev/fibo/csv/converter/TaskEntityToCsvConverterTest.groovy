package by.vorakh.dev.fibo.csv.converter

import by.vorakh.dev.fibo.base.entity.TaskEntity
import by.vorakh.dev.fibo.base.entity.TaskStatus
import spock.lang.Specification

class TaskEntityToCsvConverterTest extends Specification {

    def "have #csv received by converting the #task entity"() {

        expect:
            TaskEntityToCsvConverter.convert(task) == csv
        where:
            task                                                                               || csv
            new TaskEntity(1, 2, TaskStatus.COMPLETED, 1593024253765L, 1593025749413L, "0, 1") || "1,2,COMPLETED,1593024253765,1593025749413,\"0, 1\""
            new TaskEntity(2, 4, TaskStatus.PROCESSING, 1593024256765L, 0L, null)              || "2,4,PROCESSING,1593024256765,0,null"
            new TaskEntity(3, 5, TaskStatus.FAILED, 1593024753765L, 0L, null)                  || "3,5,FAILED,1593024753765,0,null"
            new TaskEntity(4, 15, TaskStatus.CREATED, 1593024953765L, 0L, null)                || "4,15,CREATED,1593024953765,0,null"
    }
}
