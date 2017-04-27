package com.baiheplayer.bbs.inter;

import com.baiheplayer.bbs.bean.LocalInfo;

/**
 * 回调Token获取成功，用来监听Token是否下载完成
 * Created by Administrator on 2017/1/14.
 */

public interface ITokenFinish {
    void isOk(Boolean success);
}
