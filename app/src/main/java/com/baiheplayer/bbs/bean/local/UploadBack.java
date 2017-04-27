package com.baiheplayer.bbs.bean.local;

import com.baiheplayer.bbs.bean.DataBack;

import java.util.List;

/**
 * Created by Administrator on 2017/2/9.
 */

public class UploadBack extends DataBack {
    private Data data;

    private class Data{
        private String batchId;
        private List<UpLoadImage> files;

    }
    public String getBatchId() {
        return data.batchId;
    }

    public void setBatchId(String batchId) {
        this.data.batchId = batchId;
    }

    public List<UpLoadImage> getFiles() {
        return data.files;
    }

    public void setFiles(List<UpLoadImage> files) {
        this.data.files = files;
    }
}
