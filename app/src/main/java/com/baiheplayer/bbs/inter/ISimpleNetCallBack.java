package com.baiheplayer.bbs.inter;


/**
 * 回调XUtils的下载结果
 * Created by Administrator on 2016/11/23.
 */

public interface ISimpleNetCallBack<T> {
    void back(T resultInfo);

    void fail(boolean isOnCallBack);
}
