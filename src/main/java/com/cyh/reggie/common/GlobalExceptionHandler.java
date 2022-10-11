package com.cyh.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/*
全局异常处理器、底层代理；
annotations = {RestController.class, Controller.class},表示拦截加了这两个注解的类；
@ResponseBody,最终返回JSON数据；

类上添加需要拦截的Controller注解，方法上添加具体的拦截异常类
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {


    /***
     * 进行异常处理方法，一旦抛出注解中的异常
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        log.info(ex.getMessage());
        //根据获取的异常信息中的关键字判断异常类型
        if(ex.getMessage().contains("Duplicate entry")){
            //Duplicate entry违反唯一性约束，
            //将Duplicate entry 'cyyhhh' for key 'employee.idx_username'根据空格分割，第二位就是重复的ID
            String[] split = ex.getMessage().split(" ");
            String msg = split[2] + "已经存在";
            return R.error(msg);
        }
        return R.error("未知错误");
    }


    /**
     * 捕获自定义的业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ex){
        log.info(ex.getMessage());
        //返回业务异常信息
        return R.error(ex.getMessage());
    }
}
