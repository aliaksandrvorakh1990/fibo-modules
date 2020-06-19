package by.vorakh.dev.fibo.fibo_counter.repository.impl;

import by.vorakh.dev.fibo.fibo_counter.repository.TaskRepository;
import by.vorakh.dev.fibo.fibo_counter.repository.entity.TaskEntity;
import by.vorakh.dev.fibo.fibo_counter.repository.impl.rowmapper.TaskEntityRowMapper;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

@RequiredArgsConstructor
public class JdbcTaskRepository implements TaskRepository {

    private final static String CREATE_TASK =
            "INSERT INTO tasks (number, isCompleted, startProcessing) VALUES (?, ?, ?)";
    private final static String SELECT_TASK_BY_ID = "SELECT * FROM tasks WHERE task_id = ?";
    private final static String ADD_FINISH_PROCESSING_TIME =
            "UPDATE tasks SET isCompleted = ? , finishProcessing = ? WHERE task_id = ?";

    private final JdbcTemplate jdbcTemplate;
    private final Executor repositoryExecutor;

    @Override
    public CompletableFuture<TaskEntity> create(@NotNull TaskEntity task) {

        return CompletableFuture.supplyAsync(
            () -> {
                KeyHolder keyHolder = new GeneratedKeyHolder();
                jdbcTemplate.update(connection -> {
                        PreparedStatement ps = connection
                            .prepareStatement(CREATE_TASK, RETURN_GENERATED_KEYS);
                        ps.setInt(1, task.getNumber());
                        ps.setBoolean(2, task.isCompleted());
                        ps.setTimestamp(3, task.getStartProcessing());
                        return ps;
                    },
                    keyHolder
                );
                Long taskId = keyHolder.getKey().longValue();
                task.setId(taskId);
                return task;
            },
            repositoryExecutor
        );
    }

    @Override
    public CompletableFuture<TaskEntity> getBy(long taskId) {

        return CompletableFuture.supplyAsync(
            () -> {
                List<TaskEntity> listWithATask = jdbcTemplate.query(
                    connection -> {
                        PreparedStatement ps = connection.prepareStatement(SELECT_TASK_BY_ID);
                        ps.setLong(1, taskId);
                        return ps;
                    },
                    new TaskEntityRowMapper()
                );
                TaskEntity taskById = (listWithATask.isEmpty()) ? null : listWithATask.get(0);
                return taskById;
            },
            repositoryExecutor
        );
    }

    @Override
    public CompletableFuture<Void> update(long taskId, @NotNull Timestamp finishProcessing) {

        return CompletableFuture.runAsync(
            () -> {
                boolean taskCompleted = true;
                jdbcTemplate.update(connection -> {
                    PreparedStatement ps = connection
                        .prepareStatement(ADD_FINISH_PROCESSING_TIME);
                    ps.setBoolean(1, taskCompleted);
                    ps.setTimestamp(2, finishProcessing);
                    ps.setLong(3, taskId);
                    return ps;
                });
            },
            repositoryExecutor
        );
    }
}
