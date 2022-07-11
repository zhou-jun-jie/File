package com.zjj.file;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.zjj.file.bean.StorageBean;
import java.util.List;
import io.reactivex.functions.Consumer;

/**
 * 文件管理功能测试
 */

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ZJJ_FILE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * 获取梯口里的Image路径
     */
    public void getImagePath(View view) {
        String image = FileManager.getInstance().getDirPath("Image");
        Log.e(TAG, "Image:" + image);
    }

    /**
     * 获取梯口里的Video路径
     */
    public void getVideoPath(View view) {
        String video = FileManager.getInstance().getDirPath("Video");
        Log.e(TAG, "Video:" + video);
    }

    /**
     * 获取梯口里的Kly路径
     */
    public void getKlyPath(View view) {
        String kly = FileManager.getInstance().getDirPath("Kly");
        Log.e(TAG, "Kly:" + kly);
    }

    /**
     * 获取内存
     */
    public void getMemory(View view) {
        setData();
    }

    @SuppressLint("CheckResult")
    public void setData() {
        TextView tvName1 = findViewById(R.id.sd_name_1);
        TextView tvTotal1 = findViewById(R.id.sd_total_1);
        TextView tvUsed1 = findViewById(R.id.sd_used_1);

        FileManager.getInstance().getStorageList()
                .subscribe(storageList -> {
                    if (storageList.size() > 0) {
                        String sd_1 = storageList.get(0).getPath();
                        String name1 = sd_1 + ",hasSD:" + MemoryManager.getInstance().hasSD;
                        tvName1.setText(name1);
                        StorageBean storageBean = storageList.get(0);
                        String total1 = "total:" + Utils.getUnit(storageBean.getTotal());
                        tvTotal1.setText(total1);
                        String used1 = "used:" + Utils.getUnit(storageBean.getUsed());
                        tvUsed1.setText(used1);
                    }
                });

    }

    /**
     * 格式化所有SD卡
     * @param view
     */
    @SuppressLint("CheckResult")
    public void formatAll(View view) {
        /*FileManager.getInstance().formatAll().subscribe(
        aBoolean -> Log.e(TAG, "格式化是否成功:" + aBoolean));*/
        FileManager.getInstance().deleteAll().subscribe(new Consumer<List<Boolean>>() {
            @Override
            public void accept(List<Boolean> booleans) throws Exception {
                Log.e(TAG, "删除是否成功:" + booleans);
            }
        });
    }
}