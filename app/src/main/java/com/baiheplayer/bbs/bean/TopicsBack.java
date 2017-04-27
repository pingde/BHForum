package com.baiheplayer.bbs.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/11/30.
 */

public class TopicsBack extends DataBack {
    private Data data;

    private class Data{
        private String total;
        private String pages;
        private String current;
        private List<Topic> list;
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

    public List<Topic> getTopicList() {
        return data.list;
    }

    public void setTopicList(List<Topic> topicList) {
        this.data.list = topicList;
    }
}
