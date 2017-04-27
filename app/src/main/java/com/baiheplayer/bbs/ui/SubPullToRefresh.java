package com.baiheplayer.bbs.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

import com.handmark.pulltorefresh.library.OverscrollHelper;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.handmark.pulltorefresh.library.internal.FlipLoadingLayout;
import com.handmark.pulltorefresh.library.internal.LoadingLayout;
import com.handmark.pulltorefresh.library.internal.RotateLoadingLayout;

/**
 * Created by Administrator on 2016/12/24.
 */

public class SubPullToRefresh extends PullToRefreshScrollView {
    public SubPullToRefresh(Context context) {
        super(context);
    }

    public SubPullToRefresh(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected LoadingLayout createLoadingLayout(Context context, Mode mode, TypedArray attrs) {
//        return super.createLoadingLayout(context, mode, attrs);
//        RotateLoadingLayout rotate = new RotateLoadingLayout(context, mode, Orientation.VERTICAL, attrs);
//        return rotate;

        return new DefLoadingLayout(context,mode,Orientation.VERTICAL,attrs);
//        return new FlipLoadingLayout(context, mode, Orientation.VERTICAL, attrs);
    }

    @Override
    protected ScrollView createRefreshableView(Context context, AttributeSet attrs) {
//        return super.createRefreshableView(context, attrs);
        return new SubScrollView(context, attrs);
    }


}
