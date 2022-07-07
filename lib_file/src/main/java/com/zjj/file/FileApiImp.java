package com.zjj.file;

import android.annotation.SuppressLint;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;

import com.zjj.file.bean.StorageBean;

import java.io.File;
import java.nio.file.SecureDirectoryStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.BooleanSupplier;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * name：zjj
 * date：2022/7/1
 * desc：文件具体操作类
 */
public class FileApiImp implements FileApi {

    private static final String TAG = "ZJJ_FILE";

    private String savePath;
    private boolean showLog;
    private String saveName;
    private String[] dirNames;
    private float cleanPercent;
    private float retainPercent;
    private int fileSize;

    // 文件名+地址的Map
    private final HashMap<String, String> fileMap;

    public FileApiImp(boolean showLog, String saveName, String[] dirNames, float cleanPercent,
                      float retainPercent, int fileSize) {
        this.showLog = showLog;
        this.saveName = saveName;
        this.dirNames = dirNames;
        this.cleanPercent = cleanPercent;
        this.retainPercent = retainPercent;
        this.fileSize = fileSize;
        fileMap = new HashMap<>();
    }

    @Override
    public void createSavePath() {
        this.savePath = MemoryManager.getInstance().getSavePath("");
        boolean isSuccess = Utils.mkDirs(savePath);
        if (showLog) {
            Log.e(TAG, "创建文件保存目录:" + savePath + ",isSuccess:" + isSuccess);
        }
    }

    @Override
    public void createSavePath(String rootName) {
        this.savePath = MemoryManager.getInstance().getSavePath("", rootName);
        boolean isSuccess = Utils.mkDirs(savePath);
        if (showLog) {
            Log.e(TAG, "创建文件保存目录:" + savePath + ",isSuccess:" + isSuccess);
        }
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
    public String getDirPath(String dirName) {
        return fileMap.get(dirName);
    }

    @SuppressLint("CheckResult")
    @Override
    public Observable<List<String>> autoClear() {
        return Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
                    emitter.onNext(MemoryManager.getInstance().initSD(Utils.getApplicationByReflect()));
                    emitter.onComplete();
                })
                .subscribeOn(Schedulers.io())
                .filter(aBoolean -> filter0rRepeat())
                .flatMap((Function<Boolean, ObservableSource<StorageBean>>) aBoolean -> {
                    boolean hasSD = MemoryManager.getInstance().hasSD();
                    return Observable.fromIterable(MemoryManager.getInstance().getStorage(hasSD));
                }).flatMap((Function<StorageBean, ObservableSource<String>>)
                        storageBean -> setObservable(storageBean.getRootPath()))
                .flatMap((Function<String, ObservableSource<String>>) this::setObservable)
                .flatMap((Function<String, ObservableSource<String>>) this::setObservable)
                .filter(s -> {
                    Utils.deleteFileWithDir(new File(s));
                    return true;
                })
                .repeatUntil(() -> !filter0rRepeat())
                .toList().toObservable();
    }

    private Observable<String> setObservable(String dirPath) {
        List<String> pathList = Utils.getSubFolderAndFileNames(dirPath);
        if (pathList.size() <= 0) {
            return Observable.just("empty");
        }
        Collections.sort(pathList);
        for (String path : pathList) {
            List<String> list = Utils.getSubFolderAndFileNames(path);
            if (list.size() <= 0) {
                Utils.deleteFileWithDir(new File(path));
                continue;
            }
            return Observable.just(path);
        }
        return Observable.just("empty");
    }

    private boolean filter0rRepeat() {
        boolean hasSD = MemoryManager.getInstance().hasSD();
        List<StorageBean> storageList;
        long total = 0;
        long used = 0;
        if (hasSD) {
            // 判断SD卡
            storageList = MemoryManager.getInstance().getStorage(true);
            for (StorageBean storageBean : storageList) {
                total += storageBean.getTotal();
                used += storageBean.getUsed();
            }
        } else {
            // 判断内部
            storageList = MemoryManager.getInstance().getStorage(false);
            for (StorageBean storageBean : storageList) {
                total += storageBean.getTotal();
                used += storageBean.getUsed();
            }
        }
        boolean isFilterOrRepeat = used * 1.0f / total >= 0.01;
        Log.e("zjj_file", "是否超过 until:" + isFilterOrRepeat);
        return isFilterOrRepeat;
    }


    /**
     * 创建文件夹路径
     *
     * @param year  年
     * @param month 月
     * @param day   日
     */
    private void createDateDirs(int year, String month, String day) {
        // 创建年/月/日文件夹
        String yearPath = savePath + File.separator + year + "年";
        String monthPath = yearPath + File.separator + month + "月";
        String dayPath = monthPath + File.separator + day + "日";
        Utils.mkDirs(yearPath);
        Utils.mkDirs(monthPath);
        Utils.mkDirs(dayPath);
        // 创建 dirNames
        if (null != dirNames && dirNames.length > 0) {
            for (String name : dirNames) {
                String path = fileMap.get(name);
                if (TextUtils.isEmpty(path)) {
                    // 为空,直接创建文件夹
                    String dirPath = dayPath + File.separator + name;
                    boolean isSuccess = Utils.mkDirs(dirPath);
                    fileMap.put(name, dirPath);
                    if (showLog) {
                        Log.e(TAG, "创建存放路径:" + dirPath + ",是否成功:" + isSuccess);
                    }
                } else {
                    // 不为空
                    // 获取文件数量
                    int folderSize = Utils.getFolderSize(path);
                    if (folderSize >= fileSize) {
                        // 获取文件夹的编号
                        int num = Utils.getNum(path, path.length() - 1);
                        String dirPath = dayPath + File.separator + name + num;
                        boolean isSuccess = Utils.mkDirs(dirPath);
                        fileMap.put(name, dirPath);
                        if (showLog) {
                            Log.e(TAG, "创建存放路径:" + dirPath + ",是否成功:" + isSuccess);
                        }
                    }
                }
            }
        }
    }


}
