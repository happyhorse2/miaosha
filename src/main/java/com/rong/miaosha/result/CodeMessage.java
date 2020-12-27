package com.rong.miaosha.result;

public class CodeMessage {
    private int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String message;

    private CodeMessage(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private static CodeMessage USER_NOT_EXIT = new CodeMessage(5000, "yonghubcunchunzai");
}
