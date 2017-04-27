package com.baiheplayer.bbs.view;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.baiheplayer.bbs.R;

import org.xutils.DbManager;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by Administrator on 2016/12/19.
 */
@ContentView(R.layout.activity_netboom)
public class NetBoomActivity extends BaseActivity {
    @ViewInject(R.id.crow)
    private ImageView mCrow;
    private int screenWidth;
    private AnimationDrawable ad;
    @Override
    public void onView(Bundle b) {
        screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        ad = (AnimationDrawable)mCrow.getDrawable();
    }

    @Override
    protected void onResume() {
        super.onResume();
        TranslateAnimation animation = new TranslateAnimation(screenWidth,-720,0,0);
        animation.setDuration(5000);
        animation.setInterpolator(this,android.R.anim.linear_interpolator);
        animation.setRepeatMode(Animation.RESTART);
        animation.setRepeatCount(Animation.INFINITE);
        mCrow.setAnimation(animation);
        ad.start();
    }

    @Event(R.id.refresh)
    private void goback(View view){
        finish();
    }

}
