package com.book.library.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CommonUtils {
	
	public static String formatLocalDateTime(LocalDateTime dateTime, String format) {
        if (dateTime == null || format == null || format.isEmpty()) {
            throw new IllegalArgumentException("DateTime and format must not be null or empty");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return dateTime.format(formatter);
    }

}
