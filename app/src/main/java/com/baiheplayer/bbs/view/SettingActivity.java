package com.baiheplayer.bbs.view;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.baiheplayer.bbs.BuildConfig;
import com.baiheplayer.bbs.R;
import com.baiheplayer.bbs.common.CommonData;
import com.baiheplayer.bbs.utils.SaveUtils;
import com.baiheplayer.bbs.utils.Share;
import com.nostra13.universalimageloader.cache.disc.DiscCacheAware;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.xutils.DbManager;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.List;

/**
 * 设置界面
 * Created by Administrator on 2016/11/14.
 */
@ContentView(R.layout.activity_setting)
public class SettingActivity extends BaseActivity {
    @ViewInject(R.id.switch_ad)    //推送开关
    private Switch mPush;
    @ViewInject(R.id.cache_size)
    private TextView mCache;
    @ViewInject(R.id.ll_about_us)
    private LinearLayout mAboutUs;
    @ViewInject(R.id.text_version)
    private TextView mVersion;

    private final String ACCEPT_PUSH = "acceptPush";


    private DiscCacheAware discCacheAware;
    private long cache_imageLoader;
    private long cache_xutils;
    private static final int DO_CLEAR = 50;
    private static final int FINISH_CLEAR = 60;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DO_CLEAR:
                    mCache.setText("正在清理...");
                    ImageLoader.getInstance().clearDiscCache();
                    x.image().clearCacheFiles();
                    x.image().clearMemCache();
                    clearInnerCache();
                    handler.sendEmptyMessageDelayed(FINISH_CLEAR, 3000);
                    break;
                case FINISH_CLEAR:
                    mCache.setText("无缓存");
                    break;
            }
        }
    };

    @Override
    public void onView(Bundle b) {
        CloudPushService pushService = PushServiceFactory.getCloudPushService();
        boolean acceptPush = Share.getBoolean("acceptPush",true);
        mPush.setChecked(acceptPush);
        discCacheAware = ImageLoader.getInstance().getDiscCache();
        measureCache();
        mVersion.setText(BuildConfig.VERSION_NAME);
    }

    /**
     * 关闭界面
     *
     * @param view
     */
    @Event(value = {R.id.img_back, R.id.img_arrow})
    private void closeActivity(View view) {
        finish();
    }

    /**
     * 联系我们
     *
     * @param view
     */
    @Event(R.id.ll_call_us)
    private void goToSeeUs(View view) {
        startActivity(new Intent(this, TouchActivity.class));
//        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(CommonData.LINK_US)));
    }

    /**
     * 清除缓存
     *
     * @param view
     */
    @Event(R.id.ll_clear)
    private void clearCache(View view) {
        handler.sendEmptyMessage(DO_CLEAR);

    }

    private void measureCache() {
        cache_xutils = 0;
        File[] files = this.getExternalCacheDir().listFiles();
        for(File file:files){
            if(file.isDirectory()){
                for(File cache:file.listFiles()){
                    cache_xutils= cache_xutils+cache.length();
                }
            }
            cache_xutils= cache_xutils+file.length();
        }
        cache_xutils =cache_xutils+ measureSize(this.getCacheDir());
        mCache.setText(SaveUtils.formatCacheSize(cache_xutils));
    }

    private long measureSize(File file){
        long sum = 0;
        if(file.isDirectory()){
            File[] files = file.listFiles();
            for(File f:files){
                sum = measureSize(f);
            }
        } else {
            sum =sum+file.length();
        }
        return sum;
    }

    private void clearInnerCache(){
        File f = this.getCacheDir();
        if (f != null && f.exists() && f.isDirectory()) {
            for (File item : f.listFiles()) {
                item.delete();
            }
        }
    }
    @Event(R.id.ll_push)
    private void acceptAliPush(View view) {
        if(mPush.isChecked()){
            Share.putBoolean(ACCEPT_PUSH,true);
        } else {
            Share.putBoolean(ACCEPT_PUSH,false);
        }
    }
}
