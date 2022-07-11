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
     * 获取文件夹路径
     *
     * @param dirName 文件夹名称
     * @param time    毫秒值
     */
    String getDirPath(String dirName, long time);

    /**
     * 获取文件夹存储的根路径
     */
    Observable<String> getSavePath();

    /**
     * 自动清理
     */
    Observable<List<String>> autoClear();

    /**
     * 获取SD卡的内存
     */
    Observable<List<StorageBean>> getStorage();

    /**
     * 耗时操作
     * 格式化所有SD卡
     * 注意: 使用此方法,需重启设备
     */
    Observable<Boolean> formatAll();

    /**
     * 耗时操作
     * 格式化具体的SD卡
     * 注意: 使用此方法,需重启设备
     *
     * @param storageBean 内存实体类
     */
    Observable<Boolean> format(StorageBean storageBean);


    /**
     * 耗时操作
     * 删除单独的SD卡(无需重启设备)
     *
     * @param storageBean 内存的实体类
     * @return true代表成功, false代表失败
     */
    Observable<Boolean> deleteSingle(StorageBean storageBean);


    /**
     * 耗时操作
     * 删除所有的SD卡文件(无需重启设备)
     */
    Observable<List<Boolean>> deleteAll();

}
