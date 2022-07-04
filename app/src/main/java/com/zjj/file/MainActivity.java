package com.zjj.file;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;
import com.zjj.file.bean.StorageBean;
import com.zjj.file.receiver.StorageReceiver;

import java.io.File;
import java.util.LinkedHashMap;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


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
    public void createDir(View view) {
        Disposable subscribe = Observable.create(new ObservableOnSubscribe<Boolean>() {
                    @Override
                    public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                        emitter.onNext(MemoryManager.getInstance().initSD(MainActivity.this));
                    }
                }).subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        Log.e("zjj_memory","isSuccess:"+aBoolean);
                        fileApiImp.createRoot();
                    }
                });
    }

    public void createDirs(View view) {
        // TODO 测试
        fileApiImp.createDateDir();
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
        if (sdMap.size() <=0) {
            return;
        }

        String sd_1 = sdMap.keySet().iterator().next();
        tvName1.setText(sd_1+",hasSD:"+MemoryManager.getInstance().hasSD() );
        StorageBean storageBean = sdMap.get(sd_1);
        tvTotal1.setText("total:"+MemoryManager.getInstance().getUnit(storageBean.getTotal(),1000));
        tvUsed1.setText("used:"+MemoryManager.getInstance().getUnit(storageBean.getUsed(),1000));


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
                        Log.e("zjj_memory","format:"+aBoolean+",time:"+(System.currentTimeMillis()-start));
                    }
                });

    }

    public void createFile(View view) {
        Utils.createFile(Utils.getRootPath("ZJJ_TEST")+File.separator+"HaHaHa");
    }

    public void deleteDir(View view) {
        Utils.deleteFileWithDir(new File(Utils.getRootPath("ZJJ_TEST")));
    }

    public void deleteDirWithout(View view) {
        Utils.deleteFileWithoutDir(new File(Utils.getRootPath("ZJJ_TEST")));
    }
}