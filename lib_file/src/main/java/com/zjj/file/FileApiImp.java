package com.zjj.file;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;
import com.zjj.file.bean.StorageBean;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * name：zjj
 * date：2022/7/1
 * desc：文件具体操作类
 */
public class FileApiImp implements FileApi {

    private static final String TAG = "ZJJ_FILE";

    /**
     * 文件存储的根路径
     */
    private String savePath;
    /**
     * 是否显示日志
     */
    private final boolean showLog;
    private final float cleanPercent;
    private final float retainPercent;
    private final int fileSize;
    private final long clearTime;
    private final TimeUnit clearTimeUnit;

    // 文件名+地址的Map
    private final HashMap<String, String> fileMap;

    public FileApiImp(boolean showLog, float cleanPercent,
                      float retainPercent, int fileSize, long clearTime, TimeUnit clearTimeUnit) {
        this.showLog = showLog;
        this.cleanPercent = cleanPercent;
        this.retainPercent = retainPercent;
        this.fileSize = fileSize;
        this.clearTime = clearTime;
        this.clearTimeUnit = clearTimeUnit;
        fileMap = new HashMap<>();
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
    public String getDirPath(String dirName, long time) {
        return createDateDirs(dirName, time);
    }

    @Override
    public String getRootPath() {
        return savePath;
    }

    @SuppressLint("CheckResult")
    @Override
    public Observable<List<String>> autoClear() {
        return Observable.interval(1, clearTime, clearTimeUnit)
                .subscribeOn(Schedulers.io())
                .flatMap((Function<Long, ObservableSource<Boolean>>)
                        aLong -> Observable.create((ObservableOnSubscribe<Boolean>)
                                emitter -> emitter.onNext(MemoryManager.getInstance().initSD(Utils.getApplicationByReflect()))))
                .filter(aBoolean -> filter0rRepeat(true))
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
                .repeatUntil(() -> !filter0rRepeat(false))
                .toList().toObservable();
    }

    @Override
    public List<StorageBean> getStorage() {
        boolean hasSD = MemoryManager.getInstance().hasSD();
        return MemoryManager.getInstance().getStorage(hasSD);
    }

    @Override
    public Observable<Boolean> formatAll() {
        return Observable.create(emitter -> {
            MemoryManager.getInstance().formatSD();
            emitter.onNext(true);
        });
    }

    @Override
    public Observable<Boolean> format(StorageBean storageBean) {
        return Observable.create(emitter -> {
            MemoryManager.getInstance().formatSD(storageBean);
            emitter.onNext(true);
        });
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

    private boolean filter0rRepeat(boolean isFilter) {
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
        boolean isFilterOrRepeat = used * 1.0f / total >= (isFilter ? cleanPercent : retainPercent);
        if (showLog) {
            Log.e(TAG, "是否超过:" + isFilterOrRepeat);
        }
        return isFilterOrRepeat;
    }


    /**
     * 创建文件夹路径
     *
     * @param year  年
     * @param month 月
     * @param day   日
     */
    /*private String createDateDirs(int year, String month, String day) {
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
    }*/


    /**
     * @param time    毫秒值
     * @param dirName 文件夹名称
     * @return 创建文件夹
     */
    private String createDateDirs(String dirName, long time) {
        if (TextUtils.isEmpty(dirName)) {
            if (showLog) {
                Log.e(TAG, "文件夹名称为空,请检查文件夹名称");
            }
            throw new IllegalArgumentException("文件夹名称为空,请检查文件夹名称");
        }
        int year = Utils.getYear(time);
        String month = Utils.getMonth(time);
        String day = Utils.getDay(time);
        // 创建年/月/日文件夹
        String yearPath = savePath + File.separator + year + "年";
        String monthPath = yearPath + File.separator + month + "月";
        String dayPath = monthPath + File.separator + day + "日";
        Utils.mkDirs(yearPath);
        Utils.mkDirs(monthPath);
        Utils.mkDirs(dayPath);
        // 创建 dirName
        String path = fileMap.get(dirName);
        if (TextUtils.isEmpty(path)) {
            // 为空,直接创建文件夹
            String dirPath = dayPath + File.separator + dirName;
            boolean isSuccess = Utils.mkDirs(dirPath);
            fileMap.put(dirName, dirPath);
            if (showLog) {
                Log.e(TAG, "创建存放路径:" + dirPath + ",是否成功:" + isSuccess);
            }
        } else {
            // 不为空
            // 避免手动删除了该文件夹导致无法创建新的
            String newPath = Utils.isDirExist(path);
            // 获取文件数量
            int folderSize = Utils.getFolderSize(newPath);
            if (folderSize >= fileSize) {
                // 获取文件夹的编号
                int num = Utils.getNum(path, path.length() - 1);
                String dirPath = dayPath + File.separator + dirName + num;
                boolean isSuccess = Utils.mkDirs(dirPath);
                fileMap.put(dirName, dirPath);
                if (showLog) {
                    Log.e(TAG, "创建存放路径:" + dirPath + ",是否成功:" + isSuccess);
                }
            } else {
                fileMap.put(dirName, newPath);
            }
        }
        return fileMap.get(dirName);
    }
}
