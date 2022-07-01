package com.zjj.file;

import java.io.File;

/**
 * name：zjj
 * date：2022/7/1
 * desc：文件具体操作类
 */
public class FileApiImp implements FileApi {

    private static final String TAG = "File_Tag";

    // 根目录路径
    private String rootPath;

    @Override
    public void createRoot(String rootPath) {
        this.rootPath = rootPath;
        Utils.mkDirs(rootPath);
    }

    @Override
    public void createDateDir() {
        int year = Utils.getYear();
        int month = Utils.getMonth();
        int day = Utils.getDay();
        createDateDirs(year, month, day);
    }

    @Override
    public void createDateDir(long time) {
        int year = Utils.getYear(time);
        int month = Utils.getMonth(time);
        int day = Utils.getDay(time);
        createDateDirs(year, month, day);
    }

    @Override
    public void createDir(String... dirs) {

    }

    @Override
    public void deleteDir(String... dirs) {

    }

    @Override
    public void deleteAllDir() {

    }

    @Override
    public void createFile(String fileName) {

    }

    @Override
    public void deleteFile(String fileName) {

    }

    @Override
    public void deleteAllFile() {

    }

    @Override
    public void autoClear() {
        double totalSize = 1024 * 1024;
        // 拿到当前内存
        double usedSize = 1024 * 1024 * 0.9;
        // 达到 多少内存 开始清理  --> TODO maxSize = 90%
        if (usedSize / totalSize >= 0.9) {

        }
        // 清理到 多少内存        --> TODO standardSize = 50%
    }

    private void createDateDirs(int year, int month, int day) {
        String yearPath = rootPath + File.separator + year + "年";
        String monthPath = yearPath + File.separator + month + "月";
        String dayPath = monthPath + File.separator + day + "日";
        Utils.mkDirs(yearPath);
        Utils.mkDirs(monthPath);
        Utils.mkDirs(dayPath);
    }

}
