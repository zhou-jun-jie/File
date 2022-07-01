package com.zjj.file;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.storage.StorageManager;
import android.util.Log;

import com.zjj.file.bean.StorageBean;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * name：zjj
 * date：2022/7/1
 * desc：内存卡管理类
 */
public class MemoryManager {

    private static final String TAG = "MemoryManager";

    public final LinkedHashMap<String, StorageBean> sdMap;

    private StorageManager storageManager;

    private MemoryManager() {
        sdMap = new LinkedHashMap<>();
    }

    private static class MemoryHolder {
        private static final MemoryManager INSTANCE = new MemoryManager();
    }

    public static MemoryManager getInstance() {
        return MemoryHolder.INSTANCE;
    }

    /**
     * 初始化内存卡
     *
     * @param context 上下文
     */
    public boolean initSD(Context context) {
        storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        try {
            Method getVolumes = StorageManager.class.getDeclaredMethod("getVolumes");
            List<Object> getVolumeInfo = (List<Object>) getVolumes.invoke(storageManager);
            if (null != getVolumeInfo && getVolumeInfo.size() > 0) {
                StorageBean storageBean;
                for (int i = 0; i < getVolumeInfo.size(); i++) {
                    storageBean = new StorageBean();
                    Object obj = getVolumeInfo.get(i);
                    Field getType = obj.getClass().getField("type");
                    int type = getType.getInt(obj);
                    if (type == 0) {//TYPE_PUBLIC
                        //外置存储
                        Method isMountedReadable = obj.getClass().getDeclaredMethod("isMountedReadable");
                        boolean readable = (boolean) isMountedReadable.invoke(obj);
                        if (readable) {
                            Method file = obj.getClass().getDeclaredMethod("getPath");
                            File f = (File) file.invoke(obj);
                            Field getId = obj.getClass().getField("id");
                            String formatId = (String) getId.get(obj);
                            //外置存储
                            String sdPath = getPath(obj);
                            long total = f.getTotalSpace();
                            long used = total - f.getFreeSpace();
                            storageBean.setId(i + 1);
                            storageBean.setTotal(total);
                            storageBean.setUsed(used);
                            storageBean.setFormatId(formatId);
                            storageBean.setPath(sdPath);
                            Log.e("zjj_memory", "sdPath:" + sdPath + ",format:" + formatId);
                            sdMap.put(formatId, storageBean);
                        }
                    }
                }
                return true;
            }
        } catch (SecurityException e) {
            Log.e(TAG, "缺少权限：permission.PACKAGE_USAGE_STATS");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static String getPath(Object obj) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        String sdPath = "";
        Method isMountedReadable = obj.getClass().getDeclaredMethod("isMountedReadable");
        boolean readable = (boolean) isMountedReadable.invoke(obj);
        if (readable) {
            Method file = obj.getClass().getDeclaredMethod("getPath");
            File f = (File) file.invoke(obj);
            sdPath = f.getPath();
        }
        return sdPath;
    }

    public static void removePath(LinkedHashMap<String, StorageBean> sdMap, String path) {
        Set<Map.Entry<String, StorageBean>> entrySet = sdMap.entrySet();
        Iterator<Map.Entry<String, StorageBean>> iterator = entrySet.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, StorageBean> next = iterator.next();
            StorageBean value = next.getValue();
            if (null != value && value.getPath().equals(path)) {
                iterator.remove();
            }
        }
    }

    private static final String[] units = {"B", "KB", "MB", "GB", "TB"};

    /**
     * 进制转换
     */
    public String getUnit(float size, float base) {
        int index = 0;
        while (size > base && index < 4) {
            size = size / base;
            index++;
        }
        return String.format(Locale.getDefault(), " %.0f %s ", size, units[index]);
    }

    /**
     * 格式化所有SD卡
     */
    public void formatSD() {
        try {
            if (sdMap.size() <= 0) {
                return;
            }
            for (String formatId : sdMap.keySet()) {
                format(sdMap.get(formatId));
            }
        } catch (SecurityException e) {
            Log.e(TAG, "缺少权限：permission.PACKAGE_USAGE_STATS");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 格式化某一张内存卡
     *
     * @param storageBean 内存卡实体类(在界面中格式化)
     */
    public void formatSD(StorageBean storageBean) {
        try {
            if (sdMap.size() <= 0) {
                return;
            }
            format(storageBean);
        } catch (SecurityException e) {
            Log.e(TAG, "缺少权限：permission.PACKAGE_USAGE_STATS");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void format(StorageBean storageBean) {
        try {
            if (storageBean != null) {
                Method format = StorageManager.class.getDeclaredMethod("format", String.class);
                Method mount = StorageManager.class.getDeclaredMethod("mount", new Class[]{String.class});
                format.invoke(storageManager, storageBean.getFormatId());
                mount.invoke(storageManager, storageBean.getFormatId());
                Log.e("zjj_memory", "格式化卡的id是:" + storageBean.getFormatId());
            }
        } catch (Exception e) {
            if (e.getCause() instanceof SecurityException) {
                Log.e("zjj_memory", "android manifest has no permission:[清单文件缺少权限:]" + e.getCause().getMessage());
            }
            e.printStackTrace();
        }
    }

}
