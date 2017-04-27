package com.baiheplayer.bbs.bean.local;

import com.baiheplayer.bbs.bean.DataBack;

/**
 * Created by Administrator on 2017/2/10.
 */

public class VersionBack extends DataBack {
    private VersionInfo data;

    public VersionInfo getData() {
        return data;
    }

    public void setData(VersionInfo data) {
        this.data = data;
    }
}
