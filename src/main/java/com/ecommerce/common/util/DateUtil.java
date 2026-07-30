package com.ecommerce.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
@Slf4j
public class DateUtil {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String formatLocalDateTime(LocalDateTime dateTime) {
        String formatted = dateTime != null ? dateTime.format(FORMATTER) : null;
        log.debug("Formatted LocalDateTime: {}", formatted);
        return formatted;
    }

    public static LocalDateTime parseLocalDateTime(String dateTimeString) {
        LocalDateTime parsed = dateTimeString != null ? LocalDateTime.parse(dateTimeString, FORMATTER) : null;
        log.debug("Parsed LocalDateTime: {}", parsed);
        return parsed;
    }
}
