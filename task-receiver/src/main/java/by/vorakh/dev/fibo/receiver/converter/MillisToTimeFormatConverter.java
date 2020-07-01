package by.vorakh.dev.fibo.receiver.converter;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MillisToTimeFormatConverter {

    private final static DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    public static String convert(long millis) {

        long millisToNanoSeconds = TimeUnit.MILLISECONDS.toNanos(millis);
        LocalTime time =  LocalTime.ofNanoOfDay(millisToNanoSeconds);
        return time.format(TIME_FORMATTER);
    }
}
