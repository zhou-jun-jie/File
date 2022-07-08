package com.zjj.file;

import java.util.concurrent.TimeUnit;

/**
 * name：zjj
 * date：2022/7/7
 * desc：文件管理类
 */
public class FileManager {

    private FileConfig fileConfig;

    private FileManager() {
    }

    private static class FileHolder {
        private static final FileManager INSTANCE = new FileManager();
    }

    public static FileManager getInstance() {
        return FileHolder.INSTANCE;
    }

    /**
     * 初始化配置
     */
    public void init() {
        // 文件配置
        fileConfig = new FileConfig.Builder()
                .showLog(true)                          // 显示日志 "ZJJ_FILE"
                .setSaveName("ZJJ_TEST")                // 存储路径名称
                .setFileNum(1)                          // 每个文件夹的数量限制
                .setCleanPercent(0.9f)                  // 达到清理的阈值 0.9f 代表内存的90%
                .setRetainPercent(0.5f)                 // 保留的阈值 0.5f 清理达到50%时停止清理
                .setClearTime(1,TimeUnit.DAYS)
                .build();
        RxFile.getInstance().setConfig(fileConfig);
    }

    /**
     * 获取当前存储路径
     */
    public String getRootPath() {
        return RxFile.getInstance().getRootPath();
    }

    /**
     * 获取具体的文件夹(默认以当前的毫秒值来设置)
     *
     * @param dirName 文件夹名称
     */
    public String getDirPath(String dirName) {
        return RxFile.getInstance().getDirPath(dirName, System.currentTimeMillis());
    }

    /**
     * 获取具体的文件及
     *
     * @param dirName 文件夹名称
     * @param time    毫秒值
     */
    public String getDirPath(String dirName, long time) {
        return RxFile.getInstance().getDirPath(dirName, time);
    }


}
