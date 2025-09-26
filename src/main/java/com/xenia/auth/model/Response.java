package com.xenia.auth.model;

public class Response<T> {
    private boolean success;
    private String error;
    private T data;

    public Response(boolean success, String error, T data) {
        this.success = success;
        this.error = error;
        this.data = data;
    }

    public static <T> Response<T> success(T data) {
        return new Response<>(true, null, data);
    }

    public static <T> Response<T> error(String message) {
        return new Response<>(false, message, null);
    }

    public boolean isSuccess() { return success; }
    public String getError() { return error; }
    public T getData() { return data; }
}