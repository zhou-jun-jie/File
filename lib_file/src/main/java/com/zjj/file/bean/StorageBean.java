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
}