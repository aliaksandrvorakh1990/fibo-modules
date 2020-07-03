package by.vorakh.dev.fibo.service.converter;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Instant;
import static java.time.ZoneOffset.UTC;

import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TimeInMillisToUtcDateTimeConverter {

    private final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy'T'HH:mm:ssZ");

    public static String convertUtcDateTimeFormat(long epochMilli){

        return Instant.ofEpochMilli(epochMilli).atOffset(UTC).format(DATE_TIME_FORMATTER);
    }

}
