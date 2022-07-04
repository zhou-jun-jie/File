package com.zjj.file;

import android.text.TextUtils;
import android.util.Log;

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
        boolean isSuccess = Utils.mkDirs(rootPath);
        Log.e("zjj_memory", "path:" + rootPath + ",isSuccess:" + isSuccess);
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
        for (String dir : dirs) {
            Utils.mkDirs(dir);
        }
    }

    @Override
    public void deleteDir(String filePath) {
        Utils.deleteFileWithDir(new File(filePath));
    }


    @Override
    public File createFile(String filePath) {
        return Utils.createFile(filePath);
    }

    @Override
    public void deleteFile(String fileName) {
        Utils.deleteFile(fileName);
    }


    @Override
    public void autoClear() {
        // TODO 获取外部和内部存储的大小


        // 拿到当前内存
        double usedSize = 1024 * 1024 * 0.9;
        // 达到 多少内存 开始清理  --> TODO maxSize = 90%

        // 清理到 多少内存        --> TODO standardSize = 50%
    }


    /**
     * 创建文件夹路径
     *
     * @param year  年
     * @param month 月
     * @param day   日
     */
    private void createDateDirs(int year, int month, int day) {
        String yearPath = rootPath + File.separator + year + "年";
        String monthPath = yearPath + File.separator + month + "月";
        String dayPath = monthPath + File.separator + day + "日";
        // 创建 Image Video KlyImage
        String imagePath = dayPath + File.separator + "Image";
        String videoPath = dayPath + File.separator + "Video";
        String klyPath = dayPath + File.separator + "KlyImage";
        Utils.mkDirs(yearPath);
        Utils.mkDirs(monthPath);
        Utils.mkDirs(dayPath);
        Utils.mkDirs(imagePath);
        Utils.mkDirs(videoPath);
        Utils.mkDirs(klyPath);
    }

}
