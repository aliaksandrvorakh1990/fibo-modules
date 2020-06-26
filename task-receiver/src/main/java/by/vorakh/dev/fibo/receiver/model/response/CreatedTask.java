package by.vorakh.dev.fibo.receiver.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class CreatedTask {

    private long id;
    private Timestamp startProcessing;
}
