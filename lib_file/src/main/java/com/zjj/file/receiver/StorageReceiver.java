package com.zjj.file.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.zjj.file.MemoryManager;

/**
 * name：zjj
 * date：2022/7/1
 * desc：内存卡的广播
 */
public class StorageReceiver {

    private BroadcastReceiver mReceiver;

    private Context mContext;

    public void init(Context context) {
        mContext = context;
        // 初始化SD卡 软件启动前插入不会走广播
        MemoryManager.getInstance().initSD(context);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i("zjj_memory", "BroadcastReceiver:" + intent.getAction());
                if (Intent.ACTION_MEDIA_UNMOUNTED.equals(intent.getAction())) {
                    // 拔出状态-> MEDIA_EJECT && MEDIA_UNMOUNTED
                    Log.e("zjj_memory", "已拔出");
                    MemoryManager.getInstance().removePath(intent.getData().getPath());
                } else if (Intent.ACTION_MEDIA_MOUNTED.equals(intent.getAction())) {
                    // 插入状态-> MEDIA_CHECKING && MEDIA_MOUNTED 去刷新
                    Log.e("zjj_memory", "已插入");
                    MemoryManager.getInstance().initSD(context);
                }
                Log.i("zjj_memory", "path:" + intent.getData().getPath());
            }

        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_SHARED);//如果SDCard未安装,并通过USB大容量存储共享返回
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);//表明sd对象是存在并具有读/写权限
        filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);//SDCard已卸掉,如果SDCard是存在但没有被安装
        filter.addAction(Intent.ACTION_MEDIA_CHECKING);  //表明对象正在磁盘检查
        filter.addAction(Intent.ACTION_MEDIA_EJECT);  //物理的拔出 SDCARD
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);  //完全拔出
        filter.addDataScheme("file"); // 必须要有此行，否则无法收到广播
        context.registerReceiver(mReceiver, filter);
    }

    /**
     * 注销服务
     */
    public void unregister() {
        try {
            if (null != mReceiver) {
                mContext.unregisterReceiver(mReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
