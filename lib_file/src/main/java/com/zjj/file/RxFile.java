package com.zjj.file;

import com.zjj.file.bean.StorageBean;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;

/**
 * name：zjj
 * date：2022/7/6
 * desc：File
 */
public class RxFile implements FileApi {

    private final FileApiImp fileApiImp;

    public RxFile(FileConfig config) {
        fileApiImp = new FileApiImp(config.isShowLog(), config.getCleanPercent(),
                config.getRetainPercent(), config.getFileNum(), config.getClearTime(), config.getClearTimeUnit());
        // 初始化目录
        createSavePath(config.getSaveName());
    }

    @Override
    public void createSavePath(String rootPath) {
        fileApiImp.createSavePath(rootPath);
    }

    @Override
    public String getDirPath(String dirName, long time) {
        return fileApiImp.getDirPath(dirName, time);
    }

    @Override
    public String getRootPath() {
        return fileApiImp.getRootPath();
    }

    @Override
    public Observable<List<String>> autoClear() {
        return fileApiImp.autoClear();
    }

    @Override
    public Observable<List<StorageBean>> getStorage() {
        return fileApiImp.getStorage();
    }

    @Override
    public Observable<Boolean> formatAll() {
        return fileApiImp.formatAll();
    }

    @Override
    public Observable<Boolean> format(StorageBean storageBean) {
        return fileApiImp.format(storageBean);
    }
}
