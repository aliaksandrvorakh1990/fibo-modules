package by.vorakh.dev.fibo.task_receiver.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class CreatedTaskViewModel {

    private long id;
    private Timestamp startProcessing;

}
