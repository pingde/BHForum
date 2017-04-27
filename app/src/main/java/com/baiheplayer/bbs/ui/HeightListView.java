package com.baiheplayer.bbs.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 解决ListView嵌套在ScrollView中的滑动问题
 * Created by Administrator on 2016/11/30.
 */

public class HeightListView extends ListView {

    public HeightListView(Context context) {
        super(context);
    }

    public HeightListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HeightListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpac = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpac);
    }
}
