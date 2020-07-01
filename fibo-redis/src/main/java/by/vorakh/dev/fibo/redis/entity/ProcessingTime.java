package by.vorakh.dev.fibo.redis.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessingTime implements Serializable {

    private long taskId;
    private long timeInMillis;
}
