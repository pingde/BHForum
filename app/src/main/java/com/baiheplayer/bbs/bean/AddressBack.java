package com.baiheplayer.bbs.bean;



import java.util.List;

/**
 * Created by Administrator on 2016/12/5.
 */

public class AddressBack extends DataBack {
    private Data data;
    private class Data{
        private String total;   //总数
        private String pages;   //总页数
        private String current; //当前页面
        private List<Address> list;

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

    public List<Address> getList() {
        return data.list;
    }

    public void setList(List<Address> list) {
        this.data.list = list;
    }
}
