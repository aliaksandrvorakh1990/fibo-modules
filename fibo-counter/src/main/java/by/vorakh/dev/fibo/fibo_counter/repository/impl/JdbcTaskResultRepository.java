package by.vorakh.dev.fibo.fibo_counter.repository.impl;

import by.vorakh.dev.fibo.fibo_counter.repository.TaskResultRepository;
import by.vorakh.dev.fibo.fibo_counter.repository.entity.TaskResultEntity;
import by.vorakh.dev.fibo.fibo_counter.repository.impl.rowmapper.TaskResultEntityRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@RequiredArgsConstructor
public class JdbcTaskResultRepository implements TaskResultRepository {

    private final static String WRITE_RESULT = "INSERT INTO task_results (task_result_id, result) VALUES (?, ?)";

    private final static String GET_RESULT_BY_ID = "SELECT * FROM task_results WHERE task_result_id = ?";

    private final JdbcTemplate jdbcTemplate;
    private final Executor repositoryExecutor;

    @Override
    public CompletableFuture<TaskResultEntity> create(TaskResultEntity result) {

        return CompletableFuture.supplyAsync(
            () -> {
               jdbcTemplate.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(WRITE_RESULT);
                    ps.setLong(1, result.getId());
                    ps.setString(2, result.getResult());
                    return ps;
                });
                return result;
            },
            repositoryExecutor
        );
    }

    @Override
    public CompletableFuture<String> getResultBy(long taskId) {

        return CompletableFuture.supplyAsync(
            () -> {
                List<TaskResultEntity> listWithAResult = jdbcTemplate.query(
                    connection -> {
                        PreparedStatement ps = connection.prepareStatement(GET_RESULT_BY_ID);
                        ps.setLong(1, taskId);
                        return ps;
                    },
                    new TaskResultEntityRowMapper()
                );
                String result = (listWithAResult.isEmpty()) ? null : listWithAResult.get(0).getResult();
                return  result;
            },
            repositoryExecutor
        );
    }
}
