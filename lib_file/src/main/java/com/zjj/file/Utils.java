package com.zjj.file;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

/**
 * name：zjj
 * date：2022/7/1
 * desc：文件管理所需工具类
 */
public class Utils {

    /**
     * @param rootDir 根目录文件名称
     * @return 根目录文件夹
     */
    public static String getRootPath(String rootDir) {
        if (isSDCardMounted()) {
            //有外置sdcard 就写到外置sdcard里面
            return Environment.getExternalStorageDirectory() + File.separator + rootDir;
        } else {
            return getApplicationByReflect().getFilesDir().getPath() + File.separator + rootDir;
        }
    }

    /**
     * @return 判断是否有SD卡
     */
    public static boolean isSDCardMounted() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
                || !Environment.isExternalStorageRemovable();
    }

    /**
     * 通过反射获取application
     */
    public static Application getApplicationByReflect() {
        try {
            @SuppressLint("PrivateApi")
            Class<?> activityThread = Class.forName("android.app.ActivityThread");
            Object app = activityThread.getMethod("currentApplication").invoke(null, (Object[]) null);
            if (app == null) {
                throw new NullPointerException("u should init first");
            }
            return (Application) app;
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new NullPointerException("u should init first");
    }

    /**
     * 创建文件夹
     *
     * @param path 文件路径
     */
    public static boolean mkDirs(String path) {
        if (TextUtils.isEmpty(path)) {
            Log.e("zjj_file", "path is null,please check[路径为空,请检查路径]");
            return false;
        }
        File file = new File(path);
        if (!file.exists()) {
            return file.mkdir();
        }
        return true;
    }

    private static Calendar getCalendar(long time) {
        Date date = new Date(time);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }


    public static int getYear() {
        return getCalendar(System.currentTimeMillis()).get(Calendar.YEAR);
    }

    /**
     * @param time 毫秒值
     * @return 获取当前年份
     */
    public static int getYear(long time) {
        return getCalendar(time).get(Calendar.YEAR);
    }

    /**
     * @param time 毫秒值
     * @return 获取当前月份
     */
    public static int getMonth(long time) {
        return getCalendar(time).get(Calendar.MONTH) + 1;
    }

    public static int getMonth() {
        return getCalendar(System.currentTimeMillis()).get(Calendar.MONTH) + 1;
    }

    /**
     * @param time 毫秒值
     * @return 获取当前日
     */
    public static int getDay(long time) {
        return getCalendar(time).get(Calendar.DAY_OF_MONTH);
    }

    public static int getDay() {
        return getCalendar(System.currentTimeMillis()).get(Calendar.DAY_OF_MONTH);
    }


}
