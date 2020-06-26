package by.vorakh.dev.fibo.receiver.model.response;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ErrorMessage {

    private final String message;
}
