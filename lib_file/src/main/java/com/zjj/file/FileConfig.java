package com.zjj.file;

import android.util.Log;

import java.util.Arrays;

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
     * 存储根路径名称,默认为包名
     */
    private String saveName = Utils.getPackageName();

    /**
     * 达到多少需要清理的百分比,默认为0.9f
     */
    private float cleanPercent = 0.9f;

    /**
     * 安全的百分比,默认为0.5f
     * TODO 注意: 此参数不能小于cleanPercent
     */
    private float retainPercent = 0.5f;

    /**
     * 文件数量(超过该数量重新创建文件夹),默认为500
     */
    private int fileNum = 500;

    /**
     * 需要创建的文件夹名称--数组
     */
    private String[] dirNames;

    static class Builder {

        private FileConfig fileConfig;

        public Builder() {
            fileConfig = new FileConfig();
        }

        public Builder showLog(boolean showLog) {
            fileConfig.showLog = showLog;
            return this;
        }

        public Builder setSaveName(String saveName) {
            fileConfig.saveName = saveName;
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

        public Builder setDirs(String[] dirNames) {
            fileConfig.dirNames = dirNames;
            return this;
        }

        public FileConfig build() {
            if (fileConfig.cleanPercent <= fileConfig.retainPercent) {
                Log.e("zjj_file","需要清理的百分比不能超过保留的百分比");
                throw new IllegalArgumentException("需要清理的百分比不能超过保留的百分比");
            }
            /*if (fileConfig.dirNames.length <= 0) {
                Log.e("zjj_file","需要清理的百分比不能超过保留的百分比");
                throw new IllegalArgumentException("需要清理的百分比不能超过保留的百分比");
            }*/
            return fileConfig;
        }
    }

    public boolean isShowLog() {
        return showLog;
    }

    public void setShowLog(boolean showLog) {
        this.showLog = showLog;
    }

    public String getSaveName() {
        return saveName;
    }

    public void setSaveName(String saveName) {
        this.saveName = saveName;
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

    public String[] getDirNames() {
        return dirNames;
    }

    public void setDirNames(String[] dirNames) {
        this.dirNames = dirNames;
    }

    @Override
    public String toString() {
        return "FileConfig{" +
                "saveName='" + saveName + '\'' +
                ", cleanPercent=" + cleanPercent +
                ", retainPercent=" + retainPercent +
                ", fileNum=" + fileNum +
                ", dirNames=" + Arrays.toString(dirNames) +
                '}';
    }
}
