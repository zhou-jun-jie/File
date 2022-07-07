package com.zjj.file;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;

/**
 * name：zjj
 * date：2022/7/6
 * desc：File
 */
public class RxFile implements FileApi {

    private FileApiImp fileApiImp;

    private RxFile() {
    }

    private static class FileHolder {
        private static final RxFile INSTANCE = new RxFile();
    }

    public static RxFile getInstance() {
        return FileHolder.INSTANCE;
    }

    public void setConfig(FileConfig config) {
        fileApiImp = new FileApiImp(config.isShowLog(), config.getSaveName(), config.getCleanPercent(),
                config.getRetainPercent(), config.getFileNum(),config.getClearTime(),config.getClearTimeUnit());
        // 初始化目录
        createSavePath(config.getSaveName());
    }

    @Override
    public void createSavePath(String rootPath) {
        fileApiImp.createSavePath(rootPath);
    }

    @Override
    public void createDir(String... dirs) {
        fileApiImp.createDir(dirs);
    }

    @Override
    public void deleteDir(String filePath) {
        fileApiImp.deleteDir(filePath);
    }

    @Override
    public File createFile(String filePath) {
        return fileApiImp.createFile(filePath);
    }

    @Override
    public void deleteFile(String fileName) {
        fileApiImp.deleteFile(fileName);
    }

    @Override
    public String getDirPath(String dirName, long time) {
        return fileApiImp.getDirPath(dirName,time);
    }

    @Override
    public String getRootPath() {
        return fileApiImp.getRootPath();
    }

    @Override
    public Observable<List<String>> autoClear() {
        return fileApiImp.autoClear();
    }
}
