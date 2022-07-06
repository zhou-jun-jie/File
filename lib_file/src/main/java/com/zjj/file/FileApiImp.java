package com.zjj.file;

import android.text.TextUtils;
import android.util.FloatProperty;
import android.util.Log;
import android.view.ViewDebug;

import com.zjj.file.bean.ClearBean;
import com.zjj.file.bean.StorageBean;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * name：zjj
 * date：2022/7/1
 * desc：文件具体操作类
 *
 * TODO
 * 1. 文件夹数量可变(变量代表大于多少个文件)
 */
public class FileApiImp implements FileApi {

    private static final String TAG = "File_Tag";

    // 保存路径
    private String savePath;
    private String imagePath;
    private String videoPath;
    private String klyPath;

    @Override
    public void createSavePath() {
        this.savePath = MemoryManager.getInstance().getSavePath("");
        boolean isSuccess = Utils.mkDirs(savePath);
        Log.e("zjj_memory", "path:" + savePath + ",isSuccess:" + isSuccess);
    }

    @Override
    public void createSavePath(String rootPath) {
        this.savePath = rootPath;
        boolean isSuccess = Utils.mkDirs(rootPath);
        Log.e("zjj_memory", "path:" + rootPath + ",isSuccess:" + isSuccess);
    }

    @Override
    public void createDateDir() {
        int year = Utils.getYear();
        String month = Utils.getMonth();
        String day = Utils.getDay();
        createDateDirs(year, month, day);
    }

    @Override
    public void createDateDir(long time) {
        int year = Utils.getYear(time);
        String month = Utils.getMonth(time);
        String day = Utils.getDay(time);
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
        // 判断是否有内存
        boolean hasSD = MemoryManager.getInstance().hasSD();
        if (hasSD) {
            // 有内存卡

        } else {
            // 无内存卡
            StorageBean innerStorage = MemoryManager.getInstance().getInnerStorage();
            if (null != innerStorage) {
                long total = innerStorage.getTotal();
                long used = innerStorage.getUsed();
                if (used * 1.0f / total >= 0.9) {
                    // TODO 变量2 cleanPercent: 达到多少需要清理; 变量3 cleanRetainPercent:清理到多少的阈值
                    clear();
                }
            }
        }
    }

    // TODO 待修改-- 看MainActivity的回复
    private void clear() {


    }


    /**
     * 创建文件夹路径
     *
     * @param year  年
     * @param month 月
     * @param day   日
     */
    private void createDateDirs(int year, String month, String day) {
        String yearPath = savePath + File.separator + year + "年";
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

    /**
     * @param year  年
     * @param month 月
     * @param day   日
     */
    public void createMultiDirs(int year, String month, String day) {
        String yearPath = savePath + File.separator + year + "年";
        String monthPath = yearPath + File.separator + month + "月";
        String dayPath = monthPath + File.separator + day + "日";
        // 创建 Image Video KlyImage
        if (TextUtils.isEmpty(imagePath))
            imagePath = dayPath + File.separator + "Image";
        if (TextUtils.isEmpty(videoPath))
            videoPath = dayPath + File.separator + "Video";
        if (TextUtils.isEmpty(klyPath))
            klyPath = dayPath + File.separator + "KlyImage";

        Utils.mkDirs(yearPath);
        Utils.mkDirs(monthPath);
        Utils.mkDirs(dayPath);
        Utils.mkDirs(imagePath);
        Utils.mkDirs(videoPath);
        Utils.mkDirs(klyPath);

        int imageSize = Utils.getFolderSize(imagePath);
        int videoSize = Utils.getFolderSize(videoPath);
        int klySize = Utils.getFolderSize(klyPath);

        // TODO 变量1 fileSize(超过多少可以清理+可配置)
        if (imageSize >= 1 || videoSize >= 1 || klySize >= 1) {
            int imageNum = Utils.getNum(imagePath, imagePath.length() - 1) + 1;
            int videoNum = Utils.getNum(videoPath, videoPath.length() - 1) + 1;
            int klyNum = Utils.getNum(klyPath, klyPath.length() - 1) + 1;
            int num = Math.max(Math.max(imageNum, videoNum), klyNum);

            // TODO 本地需要记录之前存储的路径
            imagePath = imagePath + num;
            videoPath = videoPath + num;
            klyPath = klyPath + num;

            Utils.mkDirs(imagePath);
            Utils.mkDirs(videoPath);
            Utils.mkDirs(klyPath);
        }
    }


}
