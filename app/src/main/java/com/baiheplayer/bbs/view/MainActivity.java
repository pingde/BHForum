package com.baiheplayer.bbs.view;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.baiheplayer.bbs.App;
import com.baiheplayer.bbs.Constant;
import com.baiheplayer.bbs.R;
import com.baiheplayer.bbs.fragment.DeskFragment;
import com.baiheplayer.bbs.fragment.ForumFragment;
import com.baiheplayer.bbs.fragment.MineFragment;
import com.baiheplayer.bbs.fragment.NewsFragment;
import com.baiheplayer.bbs.service.TokenIntentService;
import com.baiheplayer.bbs.utils.SaveUtils;

import org.xutils.DbManager;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;


@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity {
    @ViewInject(R.id.fl_container)
    private FrameLayout fl_container;
    @ViewInject(R.id.rl_tab_news)
    private RelativeLayout mNews;
    @ViewInject(R.id.rl_tab_forum)
    private RelativeLayout mForum;
    @ViewInject(R.id.rl_tab_mine)
    private RelativeLayout mMIne;
    @ViewInject(R.id.cb_news)
    private CheckBox cNews;
    @ViewInject(R.id.cb_forum)
    private CheckBox cForum;
    @ViewInject(R.id.cb_mine)
    private CheckBox cMine;
    private FragmentManager mFragmentManager;
    private NewsFragment mNewsFragment;
    private ForumFragment mForumFragment;
    private MineFragment mMineFragment;
    private DeskFragment mDeskFragment;
    private List<Fragment> mList = new ArrayList<>();
    private List<CheckBox> mCheckBox = new ArrayList<>();
    private DbManager db;
    //屏幕宽度
    int screenWidth;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            startActivity(new Intent(MainActivity.this, PostActivity.class));
//            overridePendingTransition(R.anim.anim_post_up,R.anim.anim_post_up_out);
            overridePendingTransition(0, 0);
        }
    };

    @Override
    public void onView(Bundle b) {

        Log.i("chen", "onCtreat()执行");
//        getMapLocation();  InitActivity获取过了
        SaveUtils.initImageLoader(this);
        mFragmentManager = getSupportFragmentManager();
        mNewsFragment = new NewsFragment();
        mForumFragment = new ForumFragment();
        mMineFragment = new MineFragment();
        mList.add(mNewsFragment);
        mList.add(mForumFragment);
        mList.add(mMineFragment);
        mCheckBox.add(cNews);
        mCheckBox.add(cForum);
        mCheckBox.add(cMine);
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.add(R.id.fl_container, mList.get(1)).show(mList.get(1));
        transaction.add(R.id.fl_container, mList.get(2)).show(mList.get(2));
        transaction.add(R.id.fl_container, mList.get(0)).show(mList.get(0));
        mCheckBox.get(0).setChecked(true);
        transaction.commit();

    }

    //R.id.rl_tab_desk,
    @Event(value = {R.id.rl_tab_news, R.id.rl_tab_forum, R.id.rl_tab_mine})
    private void ClickEvent(View view) {

        switch (view.getId()) {
            case R.id.rl_tab_news:
                switchFragment(0);
                break;
            case R.id.rl_tab_forum:
                switchFragment(1);
                break;
            case R.id.rl_tab_mine:
                switchFragment(2);
                break;
        }
    }

    private void switchFragment(int position) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        for (int i = 0; i < mList.size(); i++) {
            if (position == i) {
                transaction.show(mList.get(position));
                mCheckBox.get(position).setChecked(true);
            } else {
                transaction.hide(mList.get(i));
                mCheckBox.get(i).setChecked(false);
            }
        }
        transaction.commit();
    }

    /**
     * 获取经纬度
     */
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationoption;

    private void getMapLocation() {
        mLocationoption = new AMapLocationClientOption();
        mLocationClient = new AMapLocationClient(this);
        mLocationoption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationoption.setInterval(2000);
        mLocationClient.setLocationOption(mLocationoption);
        mLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation.getErrorCode() == 0) {
                    double lon = aMapLocation.getLongitude();
                    double lat = aMapLocation.getLatitude();
                    if (aMapLocation.getAccuracy() < 5.0) {
                        mLocationClient.stopLocation();
                        mLocationClient.onDestroy();
                        Log.i("chen", "经度" + lon);
                        Log.i("chen", "纬度" + lat);
                        Constant.longitude = String.valueOf(lon);
                        Constant.latitude = String.valueOf(lat);
                    }
                }
            }
        });
        mLocationClient.startLocation();
    }


    /**
     * 退出App
     */
    private long exitTime = 0;

//    @Override
//    public void onBackPressed() {
//        if ((System.currentTimeMillis() - exitTime) > 2000) {
//            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
//            exitTime = System.currentTimeMillis();
//        } else {
//            App app = (App) getApplication();
//            app.finishAllActivity();
//            stopService(new Intent().setClass(this, TokenIntentService.class));
//            super.onBackPressed();
//        }
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            Intent i= new Intent(Intent.ACTION_MAIN);  //主启动，不期望接收数据
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //新的activity栈中开启，或者已经存在就调到栈前
            i.addCategory(Intent.CATEGORY_HOME);       //添加种类，为设备首次启动显示的页面
            startActivity(i);
        }
        return super.onKeyDown(keyCode, event);
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


}
