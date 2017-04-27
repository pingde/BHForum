package com.baiheplayer.bbs.ui;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by Administrator on 2016/12/19.
 */

public class SubScrollView extends ScrollView {

    public SubScrollView(Context context) {
        super(context);
    }

    public SubScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int computeScrollDeltaToGetChildRectOnScreen(Rect rect) {
//        return super.computeScrollDeltaToGetChildRectOnScreen(rect);
        return 0;
    }
}
