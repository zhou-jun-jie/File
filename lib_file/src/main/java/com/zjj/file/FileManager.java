package com.zjj.file;

/**
 * name：zjj
 * date：2022/7/1
 * desc：文件管理类
 */
public class FileManager {

    private FileManager() {}

    private static class FileHolder {
        private static final FileManager INSTANCE = new FileManager();
    }

    public static FileManager getInstance() {
        return FileHolder.INSTANCE;
    }

    public void init() {

    }

}
