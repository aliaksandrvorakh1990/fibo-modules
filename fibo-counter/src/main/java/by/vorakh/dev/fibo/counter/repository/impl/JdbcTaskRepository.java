package by.vorakh.dev.fibo.counter.repository.impl;

import by.vorakh.dev.fibo.counter.exception.NoPresentIdException;
import by.vorakh.dev.fibo.counter.exception.WrongTaskStatusException;
import by.vorakh.dev.fibo.counter.repository.TaskRepository;
import by.vorakh.dev.fibo.counter.repository.entity.TaskEntity;
import by.vorakh.dev.fibo.counter.repository.entity.TaskStatus;
import by.vorakh.dev.fibo.counter.validation.TaskStatusValidator;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.util.concurrent.CompletableFuture.runAsync;

@RequiredArgsConstructor
public class JdbcTaskRepository implements TaskRepository {

    private final static RowMapper<TaskEntity> TASK_ROW_MAPPER = (rs, rowNum) -> {

        long id = rs.getLong(1);
        int number = rs.getInt(2);
        TaskStatus status = Optional.ofNullable(rs.getString(3))
            .filter(TaskStatusValidator::isCorrectStatus)
            .map(TaskStatus::valueOf)
            .orElseThrow(WrongTaskStatusException::new);
        long creationTime = rs.getLong(4);
        long endTime = rs.getLong(5);
        String result = rs.getString(6);
        return new TaskEntity(id, number, status, creationTime, endTime, result);
    };

    private final static String CREATE_TASK =
        "INSERT INTO tasks (number, status, creationTime) VALUES (?, ?, ?)";
    private final static String SELECT_TASK_BY_ID =
        "SELECT task_id, number, status, creationTime, endTime, result FROM tasks WHERE task_id = ?";
    private final static String UPDATE_STATUS_FINISH_PROCESSING_RESULT_BY_TASK_ID =
        "UPDATE tasks SET status = ? , endTime = ?, result = ? WHERE task_id = ?";
    private final static String UPDATE_STATUS_BY_TASK_ID =
        "UPDATE tasks SET status = ? WHERE task_id = ?";

    @NotNull
    private final JdbcTemplate jdbcTemplate;
    @NotNull
    private final Executor repositoryExecutor;

    @Override
    public CompletableFuture<@NotNull TaskEntity> create(@NotNull TaskEntity task) {

        return CompletableFuture.supplyAsync(
            () -> {
                KeyHolder keyHolder = new GeneratedKeyHolder();

                jdbcTemplate.update(connection -> {
                        PreparedStatement ps = connection.prepareStatement(CREATE_TASK, RETURN_GENERATED_KEYS);

                        ps.setInt(1, task.getNumber());
                        ps.setString(2, task.getStatus().toString());
                        ps.setLong(3, task.getCreationTime());
                        return ps;
                    },
                    keyHolder
                );

                long taskId = Optional.ofNullable(keyHolder.getKey().longValue())
                    .map(Number::longValue)
                    .orElseThrow(NoPresentIdException::new);

                task.setId(taskId);

                return task;
            },
            repositoryExecutor
        );
    }

    @Override
    public CompletableFuture<@Nullable TaskEntity> getBy(long taskId) {

        return CompletableFuture.supplyAsync(
            () -> {
                try {
                    return jdbcTemplate.queryForObject(SELECT_TASK_BY_ID, TASK_ROW_MAPPER, taskId);
                } catch (DataAccessException exception) {
                    return null;
                }
            },
            repositoryExecutor
        );
    }

    @Override
    public CompletableFuture<Void> update(
        long taskId,
        long endTime,
        @NotNull TaskStatus status,
        @NotNull String result
    ) {

        return runAsync(
            () -> jdbcTemplate.update(UPDATE_STATUS_FINISH_PROCESSING_RESULT_BY_TASK_ID,
                status.toString(), endTime, result, taskId),
            repositoryExecutor
        );
    }

    @Override
    public CompletableFuture<Void> update(long taskId, @NotNull TaskStatus status) {

        return runAsync(
            () -> jdbcTemplate.update(UPDATE_STATUS_BY_TASK_ID, status.toString(), taskId),
            repositoryExecutor
        );
    }
}
