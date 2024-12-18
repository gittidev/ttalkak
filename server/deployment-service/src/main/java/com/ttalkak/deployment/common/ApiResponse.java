package com.ttalkak.deployment.common;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Map;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private int status;
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "OK", 200, data);
    }

    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(true, "CREATED", 201, data);
    }

    public static ApiResponse<Void> empty() {
        return new ApiResponse<>(true, "OK", 200, null);
    }

    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(true, null, 200, null);
    }

    public static <T> ApiResponse<T> fail(String message, int status) {
        return new ApiResponse<>(false, message, status, null);
    }



    public static ApiResponse<Map<String, String>> fail(String message, int status, Map<String, String> errors) {
        return new ApiResponse<>(false, message, status, errors);
    }
}
