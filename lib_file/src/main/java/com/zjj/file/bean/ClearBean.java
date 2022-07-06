package com.zjj.file.bean;

/**
 * name：zjj
 * date：2022/7/1
 * desc：清除的实体类
 */
public class ClearBean {

    // 文件大小策略
    public static final int SIZE_STRATEGY = 0;
    // 文件时间策略
    public static final int TIME_STRATEGY = 1;

    /**
     * 自动清除策略(按文件时间 or 按文件大小)
     * 目前默认为文件大小策略
     */
    private int strategy = SIZE_STRATEGY;

    public String time;  // 时间

    public long size;  // 大小



}
