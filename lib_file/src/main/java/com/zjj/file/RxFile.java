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
        fileApiImp = new FileApiImp(config.isShowLog(), config.getSaveName(), config.getDirNames(), config.getCleanPercent(),
                config.getRetainPercent(), config.getFileNum());
    }


    @Override
    public void createSavePath() {
        fileApiImp.createSavePath();
    }

    @Override
    public void createSavePath(String rootPath) {
        fileApiImp.createSavePath(rootPath);
    }

    @Override
    public void createDateDir() {
        fileApiImp.createDateDir();
    }

    @Override
    public void createDateDir(long time) {
        fileApiImp.createDateDir(time);
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
    public String getDirPath(String dirName) {
        return fileApiImp.getDirPath(dirName);
    }

    @Override
    public Observable<List<String>> autoClear() {
        return fileApiImp.autoClear();
    }
}
