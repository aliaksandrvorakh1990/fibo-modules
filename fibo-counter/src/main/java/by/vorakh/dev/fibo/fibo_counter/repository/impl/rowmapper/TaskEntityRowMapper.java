package by.vorakh.dev.fibo.fibo_counter.repository.impl.rowmapper;

import by.vorakh.dev.fibo.fibo_counter.repository.entity.TaskEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class TaskEntityRowMapper implements RowMapper<TaskEntity> {

    @Override
    public TaskEntity mapRow(ResultSet rs, int rowNum) throws SQLException {

        long id = rs.getLong("task_id");
        int number = rs.getInt("number");
        boolean isCompleted = rs.getBoolean("isCompleted");
        Timestamp startProcessing = rs.getTimestamp("startProcessing");
        Timestamp finishProcessing = rs.getTimestamp("finishProcessing");
        TaskEntity task = new TaskEntity(number, startProcessing);
        task.setId(id);
        task.setCompleted(isCompleted);
        task.setFinishProcessing(finishProcessing);
        return task;
    }
}
