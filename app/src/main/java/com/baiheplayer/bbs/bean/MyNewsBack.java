package com.baiheplayer.bbs.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/2/13.
 */

public class MyNewsBack extends DataBack {
    private Data data;
    private class Data {
        private String total;
        private String pages;
        private String current;
        List<MyNews> list;
    }
    public List<MyNews> getList() {
        return data.list;
    }

    public void setList(List<MyNews> list) {
        this.data.list = list;
    }

    public String getTotal() {
        return data.total;
    }

    public void setTotal(String total) {
        this.data.total = total;
    }

    public String getPages() {
        return data.pages;
    }

    public void setPages(String pages) {
        this.data.pages = pages;
    }

    public String getCurrent() {
        return data.current;
    }

    public void setCurrent(String current) {
        this.data.current = current;
    }
}
