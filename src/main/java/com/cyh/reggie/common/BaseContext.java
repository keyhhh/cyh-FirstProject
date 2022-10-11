package com.cyh.reggie.common;


/**
 * threadLocal线程的单独存储空间、方便线程之间传输信息
 */
public class BaseContext {

    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id){
         threadLocal.set(id);
    }

    public static Long getCurrentId(){
       return threadLocal.get();
    }
}
