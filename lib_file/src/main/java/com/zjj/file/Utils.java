package com.zjj.file;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * name：zjj
 * date：2022/7/1
 * desc：文件管理所需工具类
 */
public class Utils {

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
     * 获取当前应用版本名
     */
    public static String getPackageName() {
        String packageName = "MaxVision";
        PackageManager pm = getApplicationByReflect().getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getApplicationByReflect().getPackageName(), 0);
            packageName = packageInfo.packageName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageName;
    }

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
     * 创建文件夹
     *
     * @param path 文件路径
     */
    public static boolean mkDirs(String path) {
        if (TextUtils.isEmpty(path)) {
            Log.e("zjj_file", "path is null,please check[路径为空,请检查路径]");
            return false;
        }
        try {
            File file = new File(path);
            if (!file.exists()) {
                return file.mkdir();
            }
        } catch (Exception e) {
            Log.e("zjj_memory", "异常:" + e.getCause());
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 创建文件
     *
     * @param path 文件路径
     * @return 文件File
     */
    public static File createFile(String path) {
        if (!TextUtils.isEmpty(path)) {
            File file = new File(path);
            File parentFile = file.getParentFile();
            if (null != parentFile && !parentFile.exists()) {
                parentFile.mkdirs();
            }
            if (!file.exists()) {
                try {
                    if (file.createNewFile()) {
                        return file;
                    } else {
                        return null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            } else {
                return file;
            }
        }
        return null;
    }

    /**
     * 递归删除文件夹中的文件，含文件夹
     */
    public static void deleteFileWithDir(File dir) {
        if (null == dir || !dir.exists() || !dir.isDirectory()) return;
        for (File file : dir.listFiles()) {
            if (file.isFile()) file.delete(); // 删除所有文件
            else if (file.isDirectory()) deleteDirWithFile(file); //文件夹继续递归
        }
        dir.delete();
    }

    /**
     * 递归删除文件夹中的文件，不含文件夹
     */
    public static void deleteFileWithoutDir(File dir) {
        if (null == dir || !dir.exists() || !dir.isDirectory()) return;
        for (File file : dir.listFiles()) {
            if (file.isFile()) file.delete(); // 删除所有文件
            else if (file.isDirectory()) deleteFileWithoutDir(file); //文件夹继续递归
        }
    }

    private static boolean deleteDirWithFile(File dir) {
        if (null == dir || !dir.exists())
            return false;
        for (File file : dir.listFiles()) {
            if (file.isFile()) file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDirWithFile(file); // 递规的方式删除文件夹
        }
        return dir.delete();// 删除目录本身
    }

    /**
     * 删除文件
     *
     * @param path 文件路径
     * @return 是否成功
     */
    public static boolean deleteFile(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        File file = new File(path);
        return file.delete();
    }

    private static Calendar getCalendar(long time) {
        Date date = new Date(time);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    /**
     * @param time 毫秒值
     * @return 获取当前年份
     */
    public static int getYear(long time) {
        return getCalendar(time).get(Calendar.YEAR);
    }

    public static int getYear() {
        return getCalendar(System.currentTimeMillis()).get(Calendar.YEAR);
    }

    /**
     * @param time 毫秒值
     * @return 获取当前月份
     */
    public static String getMonth(long time) {
        int month = getCalendar(time).get(Calendar.MONTH) + 1;
        if (month < 10) {
            return "0"+month;
        }
        return String.valueOf(month);
    }

    public static String getMonth() {
        int month = getCalendar(System.currentTimeMillis()).get(Calendar.MONTH) + 1;
        if (month < 10) {
            return "0"+month;
        }
        return String.valueOf(month);
    }

    /**
     * @param time 毫秒值
     * @return 获取当前日
     */
    public static String getDay(long time) {
        int day = getCalendar(time).get(Calendar.DAY_OF_MONTH) ;
        if (day < 10) {
            return "0"+day;
        }
        return String.valueOf(day);
    }

    public static String getDay() {
        int day = getCalendar(System.currentTimeMillis()).get(Calendar.DAY_OF_MONTH) ;
        if (day < 10) {
            return "0"+day;
        }
        return String.valueOf(day);
    }


    /**
     * 获取文件夹下文件的数量
     *
     * @param folderPath 文件夹路径
     */
    public static int getFolderSize(String folderPath) {
        if (TextUtils.isEmpty(folderPath)) {
            return 0;
        }
        File file = new File(folderPath);
        if (!file.exists()) {
            return 0;
        }
        File[] files = file.listFiles();
        if (files == null || files.length == 0) {
            return 0;
        }
        return files.length;
    }

    /**
     * 判断字符串是否是数字
     *
     * @param str 字符串
     * @return true是, false不是
     */
    public static boolean isNumeric(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        for (int i = str.length(); --i >= 0; ) {
            int chr = str.charAt(i);
            if (chr < 48 || chr > 57)
                return false;
        }
        return true;
    }

    /**
     * @param path   文件路径
     * @param length 文件路径最后一个字母
     * @return 获取文件夹编号
     */
    public static int getNum(String path, int length) {
        String s = path.substring(length);
        if (isNumeric(s)) {
            return getNum(path, length - 1);
        }
        String last = path.substring(length + 1);
        if (isNumeric(last)) {
            return Integer.parseInt(last);
        }
        return 1;
    }


    public static List<String> getSubFolderAndFileNames(String folderPath) {
        List<String> fileNames = new ArrayList<>();
        File file = new File(folderPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        File[] files = file.listFiles();
        if (files != null && files.length != 0) {
            fileNames = new ArrayList<>();
            for (File value : files) {
                if (value.exists()) {
                    fileNames.add(value.getAbsolutePath());
                }
            }
        }
        return fileNames;
    }


}
