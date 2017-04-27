package com.baiheplayer.bbs.bean;


/**
 * Created by Administrator on 2016/11/29.
 */

    public class MsgBack extends DataBack {

    private Data data;

    private class Data {
        private String code;

    }

    public String getDataCode() {
        return data.code;
    }

    public void setCode(String code) {
        this.data.code = code;
    }

}
