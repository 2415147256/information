package com.lzh.servicebase.exception;

import com.lzh.commonutils.T;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author 卢正豪
 * @version 1.0
 * 异常返回类
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    // 指定出现什么异常执行这个方法
    @ExceptionHandler(Exception.class)
    // 为了返回值
    @ResponseBody
    public T error(Exception e) {
        e.printStackTrace();
        return T.error().message("执行了全局异常处理");
    }

    @ExceptionHandler(GuLiException.class)
    @ResponseBody
    public T error2(GuLiException e){
        e.printStackTrace();
        return T.error().message(e.getMsg()).code(e.getCode());
    }
}
