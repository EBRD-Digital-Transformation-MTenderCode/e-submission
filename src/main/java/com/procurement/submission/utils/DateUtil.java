package com.procurement.submission.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Date;

public class DateUtil {

    private final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(DateTimeFormatter.ISO_LOCAL_DATE)
            .appendLiteral('T')
            .append(DateTimeFormatter.ISO_LOCAL_TIME)
            .appendLiteral('Z')
            .toFormatter();

    public Date dateNowUTC() {
        return localToDate(LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));
    }

    public LocalDateTime localNowUTC() {
        return LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
    }

    public long getMilliUTC(final LocalDateTime localDateTime) {
        return localDateTime.toInstant(ZoneOffset.UTC)
                .toEpochMilli();
    }

    public LocalDateTime stringToLocal(final String dateTime) {
        return LocalDateTime.parse(dateTime, FORMATTER);
    }

    public LocalDateTime dateToLocal(final Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneOffset.UTC);
    }

    public Date localToDate(final LocalDateTime startDate) {
        return Date.from(startDate.toInstant(ZoneOffset.UTC));
    }

}
