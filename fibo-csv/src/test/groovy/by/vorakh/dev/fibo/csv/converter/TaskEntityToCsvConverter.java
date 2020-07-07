package by.vorakh.dev.fibo.csv.converter;

import by.vorakh.dev.fibo.base.entity.TaskEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class TaskEntityToCsvConverter {
    public final static String CSV_HEADER = " task_id,number,status,creationTime,endTime,result";

    public static @NotNull String convert(@NotNull TaskEntity task) {

        String task_id = String.valueOf(task.getId());
        String number = String.valueOf(task.getNumber());
        String status = task.getStatus().name();
        String creationTime = String.valueOf(task.getCreationTime());
        String endTime = String.valueOf(task.getEndTime());
        String result = Optional.ofNullable(task.getResult())
            .map(value -> String.format("\"%s\"", value))
            .orElse(null);
        String csvLine = String.join(",", task_id, number, status, creationTime, endTime, result);
        return csvLine;
    }
}
