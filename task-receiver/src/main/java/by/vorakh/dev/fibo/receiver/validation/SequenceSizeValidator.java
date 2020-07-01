package by.vorakh.dev.fibo.receiver.validation;

import by.vorakh.dev.fibo.receiver.model.payload.SequenceSize;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SequenceSizeValidator {

    private final static int LEFT_BORDER = 0;
    private final static int RIGHT_BORDER = 2000;

    public static boolean isCorrectSize(@NotNull SequenceSize sequenceSize) {

        return Optional.ofNullable(sequenceSize)
            .map(SequenceSize::getSize)
            .filter(size -> ((size > LEFT_BORDER) && (size <= RIGHT_BORDER)))
            .isPresent();
    }
}
