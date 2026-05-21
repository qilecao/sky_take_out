package com.sky.context;

public class BaseContext {
    /**
     * ThreadLocal 并不是一个Thread，而是Thread的局部变量。
     *
     * ThreadLocal为每个线程提供单独一份存储空间，具有线程隔离的效果，只有在线程内才能获取到对应的值，线程外则不能访问。
     *
     * ThreadLocal常用方法：
     * - public void set(T value) 设置当前线程的线程局部变量的值
     * - public T get() 返回当前线程所对应的线程局部变量的值
     * - public void remove() 移除当前线程的线程局部变量
     */

    //这里将threadLocal进行了封装，用来从token中获取当前登录用户的id
    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    //从token中获取当前登录用户的id，并存储到threadLocal的存储空间中
    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    //从threadLocal的存储空间中获取当前登录用户的id
    public static Long getCurrentId() {
        return threadLocal.get();
    }

    //从threadLocal的存储空间中移除当前登录用户的id
    public static void removeCurrentId() {
        threadLocal.remove();
    }

}
