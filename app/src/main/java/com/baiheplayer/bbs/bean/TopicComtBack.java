package com.baiheplayer.bbs.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/12/1.
 */

public class TopicComtBack extends DataBack {
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    private class Data{
        private String total;
        private String pages;
        private String current;
        private List<Comment> list;
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

    public List<Comment> getList() {
        return data.list;
    }

    public void setList(List<Comment> list) {
        this.data.list = list;
    }
}
