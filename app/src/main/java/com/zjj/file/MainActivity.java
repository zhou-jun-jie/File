package com.zjj.file;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;
import com.zjj.file.bean.StorageBean;
import com.zjj.file.receiver.StorageReceiver;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * 文件管理功能测试
 * TODO:
 *  1. 创建默认的根目录 && 创建指定名称的根目录
 *  2. 创建默认的文件夹(Image/Video) && 创建指定名称的文件夹-------------具体存放位置
 *  3. 获取SD卡的内存使用情况 && Storage/emulated/0/使用情况
 *  4. 获取inner 或者 outer 存放位置
 *  5. 根据文件大小自动清理内存
 */


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init receiver
        StorageReceiver storageReceiver = new StorageReceiver();
        storageReceiver.init(this);

        // Must be done during an initialization phase like onCreate
        AndPermission.with(this)
                .runtime()
                .permission(Permission.Group.STORAGE)
                .onGranted(permissions -> {
                    // Storage permission are allowed.
                })
                .onDenied(permissions -> {
                    // Storage permission are not allowed.
                })
                .start();
        setData();
    }

    FileApiImp fileApiImp = new FileApiImp();

    /**
     * 测试,创建文件夹几乎不耗时
     */
    // 创建根目录,保存在本地
    public void createRootDir(View view) {
        Disposable subscribe = Observable.create(new ObservableOnSubscribe<Boolean>() {
                    @Override
                    public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                        emitter.onNext(MemoryManager.getInstance().initSD(MainActivity.this));
                    }
                }).subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        Log.e("zjj_memory", "isSuccess:" + aBoolean);
                        fileApiImp.createSavePath();
                    }
                });
    }

    public void getMemory1(View view) {
        MemoryManager.getInstance().initSD(this);
        setData();
    }

    public void setData() {
        TextView tvName1 = findViewById(R.id.sd_name_1);
        TextView tvTotal1 = findViewById(R.id.sd_total_1);
        TextView tvUsed1 = findViewById(R.id.sd_used_1);

        LinkedHashMap<String, StorageBean> sdMap = MemoryManager.getInstance().sdMap;
        if (sdMap.size() <= 0) {
            return;
        }

        String sd_1 = sdMap.keySet().iterator().next();
        tvName1.setText(sd_1 + ",hasSD:" + MemoryManager.getInstance().hasSD() + ",文件数量:" + Utils.getFolderSize("/storage/emulated/0/file"));
        StorageBean storageBean = sdMap.get(sd_1);
        tvTotal1.setText("total:" + MemoryManager.getInstance().getUnit(storageBean.getTotal(), 1000));
        tvUsed1.setText("used:" + MemoryManager.getInstance().getUnit(storageBean.getUsed(), 1000));


        FileConfig fileConfig = new FileConfig.Builder()
                .setFileNum(11)
                .build();
        Log.e("zjj_config",fileConfig.toString());

    }

    private long start;

    public void format1(View view) {
        Disposable subscribe = Observable.create(new ObservableOnSubscribe<Boolean>() {
                    @Override
                    public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                        start = System.currentTimeMillis();
                        MemoryManager.getInstance().formatSD();
                        emitter.onNext(true);
                    }
                }).subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        Log.e("zjj_memory", "format:" + aBoolean + ",time:" + (System.currentTimeMillis() - start));
                    }
                });

    }

    public void createDir(View view) {
        fileApiImp.createDateDir();
    }

    String rootPath = "/storage/emulated/0/ZJJ_TEST";

    @SuppressLint("CheckResult")
    public void saveFile(View view) {

        List<String> subFolderAndFileNames = Utils.getSubFolderAndFileNames(rootPath);
        Collections.sort(subFolderAndFileNames);
        Observable.fromIterable(subFolderAndFileNames)
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<String, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(String s) throws Exception {
                        List<String> months = Utils.getSubFolderAndFileNames(s);
                        Collections.sort(months);
                        return Observable.fromIterable(months);
                    }
                }).flatMap(new Function<String, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(String s) throws Exception {
                        List<String> days = Utils.getSubFolderAndFileNames(s);
                        Collections.sort(days);
                        return Observable.fromIterable(days);
                    }
                }).filter(new Predicate<String>() {
                    @Override
                    public boolean test(String s) throws Exception {


                        // 获取大小
                        String fileSize = FileSizeUtil.getAutoFileOrFilesSize(rootPath);
                        Log.e("zjj_file","filter:"+fileSize.compareTo("300MB")+",file:"+s);
                        return fileSize.compareTo("300MB") > 0;
                    }
                }).subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        // 删除文件
                        Utils.deleteFileWithDir(new File(s));
                        Log.e("zjj_file","consumer:"+s);
                    }
                });
    }


    public void createDirWithNum(View view) {
        fileApiImp.createMultiDirs(Utils.getYear(),Utils.getMonth(),Utils.getDay());
    }

    public void deleteDir(View view) {
        Utils.deleteFile("/storage/emulated/0/ZJJ_TEST/2020年/12月/15日");
    }
}