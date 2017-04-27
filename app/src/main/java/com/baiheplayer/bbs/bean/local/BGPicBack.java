package com.baiheplayer.bbs.bean.local;

import com.baiheplayer.bbs.bean.DataBack;

/**
 * Created by Administrator on 2016/12/7.
 */

public class BGPicBack extends DataBack {
    private Data data;
    private class Data{
        private String background;

    }
    public String getBackground() {
        return data.background;
    }

    public void setBackground(String backgound) {
        this.data.background = backgound;
    }
}
