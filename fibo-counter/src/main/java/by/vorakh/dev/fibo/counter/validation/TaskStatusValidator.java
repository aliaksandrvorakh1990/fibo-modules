package by.vorakh.dev.fibo.counter.validation;

import by.vorakh.dev.fibo.counter.repository.entity.TaskStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TaskStatusValidator {

    public static boolean isCorrectStatus(String status) {

        return Arrays.stream(TaskStatus.values()).map(Enum::name)
            .filter(statusName -> statusName.equals(status)).count() == 1;
    }
}
