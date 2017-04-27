package com.baiheplayer.bbs.ui;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.baiheplayer.bbs.R;
import com.baiheplayer.bbs.utils.SaveUtils;

/**
 * 动态logo
 * Created by Administrator on 2017/1/17.
 */

public class LogoView extends RelativeLayout {
    private ImageView logo_main;
    private ImageView logo_b1;
    private ImageView logo_g1;
    private ImageView logo_r1;
    private ImageView logo_b2;
    private ImageView logo_g2;
    private ImageView logo_r2;
    private AnimatorSet set;
    private ObjectAnimator shine, vanish;
    private ObjectAnimator bigger, smoller;
    private int[] startTime;
    public LogoView(Context context) {
        super(context);

    }

    public LogoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.item_logo, this, true);             //inflate(context, R.layout.item_logo,null);
        logo_main = (ImageView) view.findViewById(R.id.logo_main);
        logo_b1 = (ImageView) view.findViewById(R.id.logo_b1);
        logo_r1 = (ImageView) view.findViewById(R.id.logo_r1);
        logo_g1 = (ImageView) view.findViewById(R.id.logo_g1);
        logo_g2 = (ImageView) view.findViewById(R.id.logo_g2);
        logo_r2 = (ImageView) view.findViewById(R.id.logo_r2);
        logo_b2 = (ImageView) view.findViewById(R.id.logo_b2);


    }

    public LogoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public static final int TYPE_LEFT = 1;
    public static final int TYPE_INTER = 2;
    public static final int TYPE_OUT = 3;
    public static final int TYPE_MESS = 4;

    public void logoAnimStart(int type,boolean isMax) {
//        ObjectAnimator mainAnim = ObjectAnimator.ofFloat(logo_main, "alpha", 0.1f, 1.0f,0.8f);
//        mainAnim.setDuration(5000);
//        mainAnim.start();
        int[] seq =getSequenceTime(type);
        shiningStar(logo_b1, seq[0], isMax);
        shiningStar(logo_r1, seq[1], isMax);
        shiningStar(logo_g1, seq[2], isMax);
        shiningStar(logo_g2, seq[3], isMax);
        shiningStar(logo_r2, seq[4], isMax);
        shiningStar(logo_b2, seq[5], isMax);
        AlphaAnimation alpha = new AlphaAnimation(0,1);
        alpha.setDuration(4000);
        alpha.setFillAfter(true);
        alpha.start();
        logo_main.setAnimation(alpha);
        startTime = getSequenceTime(type);
    }

    private void shiningStar(View view, int time,boolean isMax) {
        float lighten = (float) (Math.random());
        if(isMax) lighten =1.0f;
        ObjectAnimator shine = ObjectAnimator.ofFloat(view, "alpha", 0, lighten, 0);//
        shine.setRepeatMode(ValueAnimator.RESTART);
        shine.setRepeatCount(ValueAnimator.INFINITE);
        shine.setDuration(3000);
        shine.setCurrentPlayTime(time);
        shine.start();
    }

    private int[] getSequenceTime(int type){
        int[] time = new int[6];
        switch (type){
            case TYPE_LEFT:
                time[0] = 0 ;
                time[1] = 100 ;
                time[2] = 200 ;
                time[3] = 300 ;
                time[4] = 400 ;
                time[5] = 500 ;
                break;
            case TYPE_INTER:
                time[0] = 0 ;
                time[1] = 400 ;
                time[2] = 800 ;
                time[3] = 800 ;
                time[4] = 400 ;
                time[5] = 0 ;
                break;
            case TYPE_OUT:
                time[0] = 800 ;
                time[1] = 400 ;
                time[2] = 0 ;
                time[3] = 0 ;
                time[4] = 400 ;
                time[5] = 800 ;
                break;
            default:
            case TYPE_MESS:
                time[0] = 0 ;
                time[1] = 800 ;
                time[2] = 1000 ;
                time[3] = 400;
                time[4] = 600 ;
                time[5] = 1100 ;
                break;
        }
        return time;
    }
}
