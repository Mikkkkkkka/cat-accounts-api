package com.mikkkkkkka.common.model.dto;

public record ApiResponse<T>(
        int status,
        String message,
        String path,
        T data
) {
    public static ApiResponse<?> ok(String path, Object data) {
        return new ApiResponse<>(200, null, path, data);
    }
}
