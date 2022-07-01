package com.zjj.file;

/**
 * name：zjj
 * date：2022/7/1
 * desc：文件接口
 */
public interface FileApi {

    /**
     * 创建根目录
     *
     * @param rootPath 根目录路径
     */
    void createRoot(String rootPath);

    /**
     * 根据当前日期创建文件夹
     * 例如: 2022年7月1日,会创建2022年文件夹以及子文件夹7月以及1日文件夹
     */
    void createDateDir();

    /**
     * 根据当前日期创建文件夹
     * @param time 毫秒值
     */
    void createDateDir(long time);

    /**
     * 创建文件夹
     *
     * @param dirs 1-n个文件目录
     */
    void createDir(String... dirs);

    /**
     * 删除文件夹
     *
     * @param dirs 1-n个文件目录
     */
    void deleteDir(String... dirs);

    /**
     * 删除所有文件夹
     */
    void deleteAllDir();

    /**
     * 创建文件
     *
     * @param fileName 文件名称
     */
    void createFile(String fileName);

    /**
     * 删除文件
     *
     * @param fileName 文件名称
     */
    void deleteFile(String fileName);

    /**
     * 删除所有文件
     */
    void deleteAllFile();

    /**
     * 自动清理
     */
    void autoClear();

}
