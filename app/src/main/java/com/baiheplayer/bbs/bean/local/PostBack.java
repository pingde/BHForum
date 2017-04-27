package com.baiheplayer.bbs.bean.local;

import com.baiheplayer.bbs.bean.DataBack;

/**
 * Created by Administrator on 2017/2/9.
 */

public class PostBack extends DataBack {
    private Data data;

    private class Data{
        String  id;   //新增帖子的id

    }
    public String getId() {
        return data.id;
    }

    public void setId(String id) {
        this.data.id = id;
    }
}
