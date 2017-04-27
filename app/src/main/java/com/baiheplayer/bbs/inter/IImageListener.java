package com.baiheplayer.bbs.inter;

import android.view.View;

import java.util.List;

/**
 * 图片点击事件监听，只有这样才能拿到图片在屏幕的位置
 * Created by Administrator on 2017/1/10.
 */

public interface IImageListener {
    void onImageClick(View view, List<String> urls, int position);
}
