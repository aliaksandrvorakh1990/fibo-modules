package by.vorakh.dev.fibo.r2dbc.repository.impl;

import by.vorakh.dev.fibo.base.entity.TaskEntity;
import by.vorakh.dev.fibo.base.entity.TaskStatus;
import by.vorakh.dev.fibo.r2dbc.repository.TaskRepository;
import io.r2dbc.spi.Row;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Update;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@RequiredArgsConstructor
public class R2dbcTaskRepository implements TaskRepository {

    private final DatabaseClient databaseClient;

    private final static Function<Row, TaskEntity> taskRow = row -> {

        long id = row.get(0, Long.class);
        int number = row.get(1, Integer.class);
        TaskStatus status = TaskStatus.valueOf(row.get(2, String.class));
        long creationTime = row.get(3, Long.class);
        long endTime = (row.get(4) == null) ? 0L : row.get(4, Long.class);
        String result = row.get(5, String.class);
        return new TaskEntity(id, number, status, creationTime, endTime, result);
    };
    private final static Function<Row, Integer> idRow = row -> row.get(0, Integer.class);

    private final static String TASKS_TABLE = "tasks";

    private final static String ID_FIELD = "task_id";
    private final static String NUMBER_FIELD = "number";
    private final static String STATUS_FIELD = "status";
    private final static String CREATION_TIME_FIELD = "creationTime";
    private final static String END_TIME_FILED = "endTime";
    private final static String RESULT_FIELD = "result";

    private final static String SELECT_ALL_TASKS =
        "SELECT task_id, number, status, creationTime, endTime, result FROM tasks";

    private final static String SELECT_TASK_BY_ID =
        "SELECT task_id, number, status, creationTime, endTime, result FROM tasks WHERE task_id = %d";

    @Override
    public Flux<TaskEntity> getAll() {

        return databaseClient.execute(SELECT_ALL_TASKS).map(taskRow).all();
    }

    @Override
    public Mono<@NotNull TaskEntity> create(@NotNull TaskEntity task) {

        return databaseClient.insert().into(TASKS_TABLE)
            .value(NUMBER_FIELD, task.getNumber())
            .value(STATUS_FIELD, task.getStatus())
            .value(CREATION_TIME_FIELD, task.getCreationTime())
            .map(idRow)
            .first().map(id -> {
                task.setId(id);
                return task;
            });
    }

    @Override
    public Mono<@Nullable TaskEntity> getBy(long taskId) {

        return databaseClient.execute(() -> String.format(SELECT_TASK_BY_ID, taskId)).map(taskRow).first();
    }

    @Override
    public Mono<Integer> update(
        long taskId, long endTime, @NotNull TaskStatus status, @NotNull String result
    ) {

        return databaseClient.update().table(TASKS_TABLE)
            .using(Update.update(STATUS_FIELD, status)
                .set(END_TIME_FILED, endTime)
                .set(RESULT_FIELD, result))
            .matching(Criteria.where(ID_FIELD).is(String.valueOf(taskId)))
            .fetch()
            .rowsUpdated();
    }

    @Override
    public Mono<Integer> update(
        long taskId, @NotNull TaskStatus status
    ) {

        return databaseClient.update().table(TASKS_TABLE)
            .using(Update.update(STATUS_FIELD, status))
            .matching(Criteria.where(ID_FIELD).is(String.valueOf(taskId)))
            .fetch()
            .rowsUpdated();
    }

    @Override
    public Mono<Integer> delete(long taskId) {

        return databaseClient.delete().from(TASKS_TABLE)
            .matching(Criteria.from(Criteria.where(ID_FIELD).is(taskId)))
            .fetch()
            .rowsUpdated();
    }
}
