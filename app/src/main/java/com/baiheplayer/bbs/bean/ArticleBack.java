package com.baiheplayer.bbs.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/1/18.
 */

public class ArticleBack extends DataBack {
    private Data data;

    private class Data {
        private String total;
        private String pages;
        private String current;
        private List<Article> list;

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

    public List<Article> getList() {
        return data.list;
    }

    public void setList(List<Article> list) {
        this.data.list = list;
    }

}
