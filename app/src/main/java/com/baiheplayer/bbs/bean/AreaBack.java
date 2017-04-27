package com.baiheplayer.bbs.bean;

import android.provider.ContactsContract;

import java.util.List;

/**
 * Created by Administrator on 2016/11/30.
 */

public class AreaBack extends DataBack {
    private Data data;

    private class Data{
        private List<Province> areaList;


    }
    public List<Province> getAreaList() {
        return data.areaList;
    }

    public void setAreaList(List<Province> areaList) {
        this.data.areaList = areaList;
    }
    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

}



