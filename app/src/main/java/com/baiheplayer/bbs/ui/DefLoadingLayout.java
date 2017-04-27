package com.baiheplayer.bbs.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.baiheplayer.bbs.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.internal.FlipLoadingLayout;
import com.handmark.pulltorefresh.library.internal.LoadingLayout;

/**
 * 自定义下拉展示的控件
 * Created by Administrator on 2016/12/24.
 */

public class DefLoadingLayout extends LoadingLayout {
    static final int ROTATION_ANIMATION_DURATION = 1200;
    private final Animation mRotateAnimation;
    private final Matrix mHeaderImageMatrix;
    private final boolean mRotateDrawableWhilePulling;
    private float mRotationPivotX;
    private float mRotationPivotY;
    private CharSequence mPullLabel;
    private CharSequence mRefreshingLabel;
    private CharSequence mReleaseLabel;
    private ProgressBar progress;
    private TextView text;

    public DefLoadingLayout(Context context, PullToRefreshBase.Mode mode, PullToRefreshBase.Orientation scrollDirection, TypedArray attrs) {
        super(context, mode, scrollDirection, attrs);
        this.mRotateDrawableWhilePulling = attrs.getBoolean(com.handmark.pulltorefresh.library.R.styleable.PullToRefresh_ptrRotateDrawableWhilePulling, true);
        this.mHeaderImage.setScaleType(ImageView.ScaleType.MATRIX);
        this.mHeaderImageMatrix = new Matrix();
        this.mHeaderImage.setImageMatrix(this.mHeaderImageMatrix);
        this.mRotateAnimation = new RotateAnimation(0.0F, 720.0F, 1, 0.5F, 1, 0.5F);
        this.mRotateAnimation.setInterpolator(new LinearInterpolator());
        this.mRotateAnimation.setDuration(1200L);
        this.mRotateAnimation.setRepeatCount(-1);
        this.mRotateAnimation.setRepeatMode(1);
        FrameLayout frameLayout = (FrameLayout)this.findViewById(com.handmark.pulltorefresh.library.R.id.fl_inner);
        frameLayout.removeAllViews();
        View define = View.inflate(context, R.layout.pull_to_refresh_def,frameLayout);
        progress = (ProgressBar) define.findViewById(R.id.pb_progress);
        text = (TextView) define.findViewById(R.id.tv_text);
        this.mPullLabel = context.getString(R.string.pull_to_refresh_pull_label);
        this.mRefreshingLabel = context.getString(R.string.pull_to_refresh_refreshing_label);
        this.mReleaseLabel = context.getString(R.string.pull_to_refresh_release_label);

        setTextTypeface(Typeface.DEFAULT);
    }

    public void onLoadingDrawableSet(Drawable imageDrawable) {
        if (null != imageDrawable) {
            this.mRotationPivotX = (float) Math.round((float) imageDrawable.getIntrinsicWidth() / 2.0F);   //2.0
            this.mRotationPivotY = (float) Math.round((float) imageDrawable.getIntrinsicHeight() / 2.0F);
        }

    }

    protected void onPullImpl(float scaleOfLayout) {
//        float angle;
//        if (this.mRotateDrawableWhilePulling) {
//            angle = scaleOfLayout * 90.0F;
//        } else {
//            angle = Math.max(0.0F, Math.min(180.0F, scaleOfLayout * 360.0F - 180.0F));
//        }
//        this.mHeaderImageMatrix.setRotate(angle, this.mRotationPivotX, this.mRotationPivotY);
//        this.mHeaderImage.setImageMatrix(this.mHeaderImageMatrix);
        progress.setProgress((int)(scaleOfLayout*100));
    }

    protected void refreshingImpl() {
//        this.mHeaderImage.startAnimation(this.mRotateAnimation);

    }

    protected void resetImpl() {
//        this.mHeaderImage.clearAnimation();
//        this.resetImageRotation();

    }

    private void resetImageRotation() {
//        if (null != this.mHeaderImageMatrix) {
//            this.mHeaderImageMatrix.reset();
//            this.mHeaderImage.setImageMatrix(this.mHeaderImageMatrix);
//        }

    }

    protected void pullToRefreshImpl() {
    }

    protected void releaseToRefreshImpl() {
    }

    protected int getDefaultDrawableResId() {
        return com.handmark.pulltorefresh.library.R.drawable.default_ptr_rotate;
    }



    private void show(String info){
        Log.i("chen",info);
    }
}
