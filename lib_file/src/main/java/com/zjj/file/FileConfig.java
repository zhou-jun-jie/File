package com.zjj.file;

import android.util.Log;

import java.util.concurrent.TimeUnit;

/**
 * name：zjj
 * date：2022/7/5
 * desc：文件配置类
 */
public class FileConfig {

    /**
     * 是否显示日志
     */
    private boolean showLog;

    /**
     * 达到多少需要清理的百分比,默认为0.9f
     */
    private float cleanPercent = 0.9f;

    /**
     * 安全的百分比,默认为0.5f
     */
    private float retainPercent = 0.5f;

    /**
     * 文件数量(超过该数量重新创建文件夹),默认为500
     */
    private int fileNum = 500;

    /**
     * 自动清理的轮询时间
     */
    private long clearTime;

    /**
     * 自动清理的轮询时间单位
     */
    private TimeUnit clearTimeUnit;

    static class Builder {

        private final FileConfig fileConfig;

        public Builder() {
            fileConfig = new FileConfig();
        }

        public Builder showLog(boolean showLog) {
            fileConfig.showLog = showLog;
            return this;
        }

        public Builder setCleanPercent(float cleanPercent) {
            fileConfig.cleanPercent = cleanPercent;
            return this;
        }

        public Builder setRetainPercent(float retainPercent) {
            fileConfig.retainPercent = retainPercent;
            return this;
        }

        public Builder setFileNum(int fileNum) {
            fileConfig.fileNum = fileNum;
            return this;
        }

        public Builder setClearTime(long clearTime, TimeUnit timeUnit) {
            fileConfig.clearTime = clearTime;
            fileConfig.clearTimeUnit = timeUnit;
            return this;
        }

        public FileConfig build() {
            if (fileConfig.cleanPercent <= fileConfig.retainPercent) {
                Log.e("zjj_file", "需要清理的百分比不能超过保留的百分比");
                throw new IllegalArgumentException("需要清理的百分比不能超过保留的百分比");
            }
            return fileConfig;
        }
    }

    public boolean isShowLog() {
        return showLog;
    }

    public void setShowLog(boolean showLog) {
        this.showLog = showLog;
    }

    public float getCleanPercent() {
        return cleanPercent;
    }

    public void setCleanPercent(float cleanPercent) {
        this.cleanPercent = cleanPercent;
    }

    public float getRetainPercent() {
        return retainPercent;
    }

    public void setRetainPercent(float retainPercent) {
        this.retainPercent = retainPercent;
    }

    public int getFileNum() {
        return fileNum;
    }

    public void setFileNum(int fileNum) {
        this.fileNum = fileNum;
    }

    public long getClearTime() {
        return clearTime;
    }

    public void setClearTime(long clearTime) {
        this.clearTime = clearTime;
    }

    public TimeUnit getClearTimeUnit() {
        return clearTimeUnit;
    }

    public void setClearTimeUnit(TimeUnit clearTimeUnit) {
        this.clearTimeUnit = clearTimeUnit;
    }

    @Override
    public String toString() {
        return "FileConfig{" +
                "showLog=" + showLog +
                ", cleanPercent=" + cleanPercent +
                ", retainPercent=" + retainPercent +
                ", fileNum=" + fileNum +
                ", clearTime=" + clearTime +
                ", clearTimeUnit=" + clearTimeUnit +
                '}';
    }
}
