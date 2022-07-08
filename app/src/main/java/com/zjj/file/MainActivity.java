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

import java.io.File;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.reactivex.MaybeSource;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BooleanSupplier;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * 文件管理功能测试
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

        // 初始化操作
        FileManager.getInstance().init(this);
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






    /**
     *  todo start ---------------------测试-----------------------
     */
    /*@SuppressLint("CheckResult")
    public void test(View view) {
        delete222().subscribe(new Consumer<List<String>>() {
            @Override
            public void accept(List<String> strings) throws Exception {
                for (String path : strings) {
                    Log.e("zjj_file", "删除成功:" + path);
                }
            }
        });
    }*/

    @SuppressLint("CheckResult")
    private Observable<List<String>> delete() {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
                    @Override
                    public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                        emitter.onNext(MemoryManager.getInstance().initSD(Utils.getApplicationByReflect()));
                        emitter.onComplete();
                    }
                })
                .subscribeOn(Schedulers.io())
                /*.repeatUntil(new BooleanSupplier() {
                    @Override
                    public boolean getAsBoolean() throws Exception {
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
                        boolean b = used * 1.0f / total >= 0.01;
                        Log.e("zjj_file", "是否超过 until:" + b);
                        return !b;
                    }
                })*/
                .filter(new Predicate<Boolean>() {
                    @Override
                    public boolean test(Boolean aBoolean) throws Exception {
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
                        boolean b = used * 1.0f / total >= 0.01;
                        Log.e("zjj_file", "是否超过filter:" + b);
                        return b;
                    }
                })
                .flatMap(new Function<Boolean, ObservableSource<StorageBean>>() {
                    @Override
                    public ObservableSource<StorageBean> apply(Boolean aBoolean) throws Exception {
                        boolean hasSD = MemoryManager.getInstance().hasSD();
                        return Observable.fromIterable(MemoryManager.getInstance().getStorage(hasSD));
                    }
                }).flatMap(new Function<StorageBean, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(StorageBean storageBean) throws Exception {
                        return setObservable(storageBean.getRootPath());
                    }
                })
                .flatMap(new Function<String, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(String s) throws Exception {
                        return setObservable(s);
                    }
                }).flatMap(new Function<String, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(String s) throws Exception {
                        return setObservable(s);
                    }
                })
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(String s) throws Exception {
                        Utils.deleteFileWithDir(new File(s));
                        Log.e("zjj_file", "delete 操作");
                        Log.e("zjj_file", "打印路径:" + s);
                        return true;
                    }
                })
                .repeatUntil(new BooleanSupplier() {
                    @Override
                    public boolean getAsBoolean() throws Exception {

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
                        boolean b = used * 1.0f / total >= 0.01;
                        Log.e("zjj_file", "是否超过 until:" + b);
                        return !b;
                    }
                })
                .toList().toObservable();
    }

    @SuppressLint("CheckResult")
    private Observable<List<String>> delete222() {
        return Observable.interval(0, 10, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<Long, ObservableSource<Boolean>>() {
                    @Override
                    public ObservableSource<Boolean> apply(Long aLong) throws Exception {
                        return Observable.create(new ObservableOnSubscribe<Boolean>() {
                            @Override
                            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                                emitter.onNext(MemoryManager.getInstance().initSD(Utils.getApplicationByReflect()));
                                /*emitter.onComplete();*/
                            }
                        });
                    }
                })
                .filter(new Predicate<Boolean>() {
                    @Override
                    public boolean test(Boolean aBoolean) throws Exception {
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
                        boolean b = used * 1.0f / total >= 0.01;
                        Log.e("zjj_file", "是否超过filter:" + b);
                        return b;
                    }
                })
                .flatMap(new Function<Boolean, ObservableSource<StorageBean>>() {
                    @Override
                    public ObservableSource<StorageBean> apply(Boolean aBoolean) throws Exception {
                        boolean hasSD = MemoryManager.getInstance().hasSD();
                        return Observable.fromIterable(MemoryManager.getInstance().getStorage(hasSD));
                    }
                }).flatMap(new Function<StorageBean, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(StorageBean storageBean) throws Exception {
                        return setObservable(storageBean.getRootPath());
                    }
                })
                .flatMap(new Function<String, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(String s) throws Exception {
                        return setObservable(s);
                    }
                }).flatMap(new Function<String, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(String s) throws Exception {
                        return setObservable(s);
                    }
                })
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(String s) throws Exception {
                        Utils.deleteFileWithDir(new File(s));
                        Log.e("zjj_file", "delete 操作");
                        Log.e("zjj_file", "打印路径:" + s);
                        return true;
                    }
                })
                .repeatUntil(new BooleanSupplier() {
                    @Override
                    public boolean getAsBoolean() throws Exception {

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
                        boolean b = used * 1.0f / total >= 0.01;
                        Log.e("zjj_file", "是否超过 until:" + b);
                        return !b;
                    }
                })
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


    /**
     *  TODO 真正实现
     */
    public void createDir(View view) {
        String rootPath = FileManager.getInstance().getRootPath();
        Log.e("zjj_test","rootPath:"+rootPath);
    }

    public void getImage(View view) {
        String image = FileManager.getInstance().getDirPath("Image");
        Log.e("zjj_test","image:"+image);
    }

    public void getVideo(View view) {
        String video = FileManager.getInstance().getDirPath("Video");
        Log.e("zjj_test","Video:"+video);
    }

    public void getKly(View view) {
        String kly = FileManager.getInstance().getDirPath("Kly");
        Log.e("zjj_test","Kly:"+kly);
    }

}