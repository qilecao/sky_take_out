package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    //这个方法是全局业务异常处理器，用于统一处理项目中所有继承自 BaseException 的业务异常
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     * 捕获用户重复异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex){
        //这个异常的报错信息实例：Duplicate entry 'taihao' for key 'employee.idx_username

        //获取到异常信息
        String message = ex.getMessage();
        //判断是不是要处理的异常，这里通过开头的两个字符串来判断

        if(message.contains("Duplicate entry")){
            //如果是，则通过空格分割字符串，便于后面获取到第三个字符串：用户名
            String[] split = message.split(" ");
            //获取用户名
            String username=split[2];
            //拼接错误信息
            String mes=username+MessageConstant.ALREADY_EXISTS;
            //返回结果
            return Result.error(mes);
        }else{
            //否则，返回未知错误
            return Result.error(MessageConstant.UNKNOWN_ERROR);
        }


    }



}
