package com.rong.miaosha.exception;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.rong.miaosha.result.CodeMessage;
import com.rong.miaosha.result.Result;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


//全局异常处理器，ControllerAdvice为Controller加强版,处理异常逻辑与exceptionHandler联用。
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {
    @ExceptionHandler(value=Exception.class)
    public Result<String> exceptionHandler(HttpServletRequest request, Exception e){
        e.printStackTrace();
        if(e instanceof GlobalException) {
            GlobalException ex = (GlobalException)e;
            return Result.error(ex.getCm());
        }else if(e instanceof BindException) {//参数验证失败，抛出的异常
            BindException ex = (BindException)e;
            List<ObjectError> errors = ex.getAllErrors();
            ObjectError error = errors.get(0);
            String msg = error.getDefaultMessage();
            return Result.error(CodeMessage.BIND_ERROR.fillArgs(msg));
        }else {
            return Result.error(CodeMessage.SERVER_ERROR);
        }
    }
}