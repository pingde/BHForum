package com.baiheplayer.bbs.view;


import android.os.Bundle;

import com.baiheplayer.bbs.App;
import com.zhy.autolayout.AutoLayoutActivity;

import org.xutils.x;


/**
 * Created by Administrator on 2016/11/7.
 */

public abstract class BaseActivity extends AutoLayoutActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getInstance().addActivity(this);

        x.view().inject(this);//添加注解

        onView(savedInstanceState);
    }


    public abstract void onView(Bundle savedInstanceState);

    @Override
    protected void onResume() {
        super.onResume();
//        SkinManager.getInstance().changeSkin(App.bg);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
