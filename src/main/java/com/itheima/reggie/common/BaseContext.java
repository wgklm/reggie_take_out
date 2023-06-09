package com.itheima.reggie.common;


/**
 * 基于ThreadLocal封装工具类，用户保存获取当前登录用户id
 */
public class BaseContext {
    private  static  ThreadLocal<Long> threadLocal=new ThreadLocal<>();

    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    public static  Long getCurrenId(){
        return threadLocal.get();
    }
}
