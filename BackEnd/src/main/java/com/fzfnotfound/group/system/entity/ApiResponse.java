package com.fzfnotfound.group.system.entity;

public class ApiResponse<T> {
    private boolean success; // 表示操作是否成功
    private T data;          // 返回的数据
    private String message;  // 提示信息

    // 全参构造函数
    public ApiResponse(boolean success, T data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }

    // 成功响应的静态方法
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, "success");
    }

    // 失败响应的静态方法
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, null, message);
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
