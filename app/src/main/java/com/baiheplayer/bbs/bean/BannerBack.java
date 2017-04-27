package com.baiheplayer.bbs.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/11/30.
 */

public class BannerBack extends DataBack {
    private Data data;

    private class Data {
        List<Banner> list;


    }

    public List<Banner> getList() {
        return data.list;
    }

    public void setList(List<Banner> list) {
        this.data.list = list;
    }
    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
