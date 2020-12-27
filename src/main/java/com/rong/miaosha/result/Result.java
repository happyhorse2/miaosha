package com.rong.miaosha.result;

public class Result<T> {
    private int code;
    private String message;
    private T data;

    private Result(T data){
        this.code =200;
        this.message = "success";
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private Result(int code, String message){
        this.code = code;
        this.message = message;
    }
    private Result(CodeMessage codeMessage){
        this.code = codeMessage.getCode();
        this.message = codeMessage.getMessage();
    }

   public static <T> Result<T> success(T data){
        return new Result<T>(data);
    }
    public static <T> Result<T> error(CodeMessage codeMessage){
        return new Result<T>(codeMessage);
    }


}
