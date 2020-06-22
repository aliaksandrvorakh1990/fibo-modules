package by.vorakh.dev.fibo.fibo_counter.repository.impl.rowmapper;

import by.vorakh.dev.fibo.fibo_counter.repository.entity.TaskResultEntity;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TaskResultEntityRowMapper implements RowMapper<TaskResultEntity> {

    @Nullable
    @Override
    public TaskResultEntity mapRow(ResultSet rs, int rowNum) throws SQLException {

        Long id = rs.getLong("task_result_id");
        String result = rs.getString("result");
        return new TaskResultEntity(id, result);
    }
}
