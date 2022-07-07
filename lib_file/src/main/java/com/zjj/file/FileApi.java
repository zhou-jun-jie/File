package com.zjj.file;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;

/**
 * name：zjj
 * date：2022/7/1
 * desc：文件接口
 */
public interface FileApi {

    /**
     * 创建文件存储路径
     * 例如:包名 com.maxvision.test
     * 无SD卡路径为: storage/emulated/0/test
     * 有SD卡路径为: sd卡路径(可变的)/Android/data/com.maxvision.test
     *
     * @param rootName 文件夹路径
     */
    void createSavePath(String rootName);

    /**
     * 创建文件夹
     *
     * @param dirs 1-n个文件目录
     */
    void createDir(String... dirs);

    /**
     * 删除文件夹
     *
     * @param filePath 文件路径
     */
    void deleteDir(String filePath);


    /**
     * 创建文件
     *
     * @param filePath 文件路径
     */
    File createFile(String filePath);

    /**
     * 删除文件
     *
     * @param fileName 文件名称
     */
    void deleteFile(String fileName);

    /**
     * 获取文件夹路径
     *
     * @param dirName 文件夹名称
     * @param time    毫秒值
     */
    String getDirPath(String dirName, long time);

    /**
     * 获取文件夹存储的根路径
     */
    String getRootPath();

    /**
     * 自动清理
     */
    Observable<List<String>> autoClear();

}
