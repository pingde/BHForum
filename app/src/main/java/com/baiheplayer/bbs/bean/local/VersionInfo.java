package com.baiheplayer.bbs.bean.local;

/**
 * 版本管理
 * Created by Administrator on 2017/2/10.
 */

public class VersionInfo {
    private String newVersion;
    private boolean isEnforce;
    private String url;
    private String updateInfo;

    public String getNewVersion() {
        return newVersion;
    }

    public void setNewVersion(String newVersion) {
        this.newVersion = newVersion;
    }

    public boolean isEnforce() {
        return isEnforce;
    }

    public void setEnforce(boolean enforce) {
        isEnforce = enforce;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUpdateInfo() {
        return updateInfo;
    }

    public void setUpdateInfo(String undateInfo) {
        this.updateInfo = undateInfo;
    }
}
