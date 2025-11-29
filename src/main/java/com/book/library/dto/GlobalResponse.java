package com.book.library.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GlobalResponse<T> {
    private boolean success;
    private String message;
    private T data;

    public static <T> GlobalResponse<T> success(String message, T data) {
        return new GlobalResponse<>(true, message, data);
    }

    public static <T> GlobalResponse<T> error(String message) {
        return new GlobalResponse<>(false, message, null);
    }
}