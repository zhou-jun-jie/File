package com.zjj.file.bean;

/**
 * name: zjj
 * date：2022/7/1
 * desc: 外置存储实体类
 */
public class StorageBean {

    // sd卡编号
    private int id;
    // sd卡路径
    private String path;
    // 格式化id
    private String formatId;
    // 总
    private long total;
    // 已使用
    private long used;
    // 是否是外置SD卡
    private boolean isSD;
    // 是否是当前存储卡(互斥唯一的)
    private boolean isSave;
    // 根目录路径
    private String rootPath;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getUsed() {
        return used;
    }

    public void setUsed(long used) {
        this.used = used;
    }

    public String getFormatId() {
        return formatId;
    }

    public void setFormatId(String formatId) {
        this.formatId = formatId;
    }

    public boolean isSD() {
        return isSD;
    }

    public void setSD(boolean SD) {
        isSD = SD;
    }

    public boolean isSave() {
        return isSave;
    }

    public void setSave(boolean save) {
        isSave = save;
    }

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    @Override
    public String toString() {
        return "StorageBean{" +
                "id=" + id +
                ", path='" + path + '\'' +
                ", formatId='" + formatId + '\'' +
                ", total=" + total +
                ", used=" + used +
                ", isSD=" + isSD +
                ", isSave=" + isSave +
                ", rootPath='" + rootPath + '\'' +
                '}';
    }
}
