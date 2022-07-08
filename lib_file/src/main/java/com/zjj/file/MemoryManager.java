package com.zjj.file;

import android.content.Context;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.util.Log;

import com.zjj.file.bean.StorageBean;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
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

    private static final String TAG = "ZJJ_MEMORY";

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
                    if (type == 0 || type == 2) {
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
                            // type == 0代表为SD卡, 2 代表 /storage/emulated
                            storageBean.setSD(type == 0);
                            storageBean.setTotal(total);
                            storageBean.setUsed(used);
                            storageBean.setFormatId(formatId);
                            storageBean.setPath(sdPath);
                            if (type == 0) {
                                storageBean.setRootPath(sdPath + "/Android/data/" + Utils.getPackageName());
                            } else {
                                String packageName = Utils.getPackageName().substring(Utils.getPackageName().lastIndexOf(".") + 1);
                                storageBean.setRootPath(sdPath + "/0/"+ packageName);
                            }
                            Log.e(TAG, "sdPath:" + sdPath + ",format:" + formatId);
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

    public void removePath(String path) {
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
                Log.e(TAG, "格式化卡的id是:" + storageBean.getFormatId());
            }
        } catch (Exception e) {
            if (e.getCause() instanceof SecurityException) {
                Log.e(TAG, "android manifest has no permission:[清单文件缺少权限:]" + e.getCause().getMessage());
            }
            e.printStackTrace();
        }
    }

    /**
     * 获取是否有SD卡
     *
     * @return true代表有, false代表没有
     */
    public boolean hasSD() {
        for (String key : sdMap.keySet()) {
            StorageBean storageBean = sdMap.get(key);
            if (null != storageBean) {
                Log.e(TAG, "path:" + storageBean.getRootPath());
                return storageBean.isSD();
            }
        }
        return false;
    }

    /**
     * 获取存储位置
     *
     * @param formatId sd卡ID
     */
    public String getSavePath(String formatId) {
        if (sdMap.size() <= 0) {
            return "";
        }
        String first = sdMap.keySet().iterator().next();
        StorageBean storageBean = sdMap.get(first);
        if (sdMap.size() == 1) {
            if (null != storageBean) {
                return storageBean.getRootPath();
            }
        } else {
            for (String key : sdMap.keySet()) {
                StorageBean sb = sdMap.get(key);
                if (null != sb) {
                    boolean hasSD = sb.isSD();
                    if (hasSD && sb.getFormatId().equals(formatId)) {
                        // 之前存在的sd卡
                        return sb.getRootPath();
                    }
                }
            }
            // 默认存储在第一张
            if (null != storageBean) {
                return storageBean.getRootPath();
            }
        }
        return "";
    }

    /**
     * 获取文件夹具体路径
     *
     * @param dirName 文件夹名称
     */
    public String getSavePath(String formatId, String dirName) {
        if (!TextUtils.isEmpty(dirName)) {
            return getSavePath(formatId);
        }
        return "";
    }


    /**
     * 获取内置内存大小
     */
    public StorageBean getInnerStorage() {
        for (String key : sdMap.keySet()) {
            StorageBean storageBean = sdMap.get(key);
            if (null != storageBean && !storageBean.isSD()) {
                return storageBean;
            }
        }
        return null;
    }

    /**
     * 获取sd卡内存大小
     */
    public List<StorageBean> getOuterStorage() {
        List<StorageBean> list = new ArrayList<>();
        for (String key : sdMap.keySet()) {
            StorageBean storageBean = sdMap.get(key);
            if (null != storageBean && storageBean.isSD()) {
                list.add(storageBean);
            }
        }
        return list;
    }

    /**
     * 获取sd卡内存大小
     */
    public List<StorageBean> getStorage(boolean hasSD) {
        List<StorageBean> list = new ArrayList<>();
        for (String key : sdMap.keySet()) {
            StorageBean storageBean = sdMap.get(key);
            if (null != storageBean && (hasSD == storageBean.isSD())) {
                list.add(storageBean);
            }
        }
        return list;
    }


}
