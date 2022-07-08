package com.zjj.file;

import com.zjj.file.bean.StorageBean;
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

    /**
     * 获取SD卡的内存
     */
    List<StorageBean> getStorage();

    /**
     * 格式化所有SD卡
     */
    Observable<Boolean> formatAll();

    /**
     * 格式化具体的SD卡
     *
     * @param storageBean 内存实体类
     */
    Observable<Boolean> format(StorageBean storageBean);
}
