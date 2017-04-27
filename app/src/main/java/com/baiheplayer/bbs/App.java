package com.baiheplayer.bbs;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.baiheplayer.bbs.utils.FileUtils;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;


import org.xutils.DbManager;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/7.
 */

public class App extends Application {

    {
        PlatformConfig.setWeixin("wx967daebe835fbeac","5bb696d9ccd75a38c8a0bfe0675559b3");
        PlatformConfig.setQQZone("100424468","c7394704798a158208a74ab60104f0ba");
        PlatformConfig.setSinaWeibo("3921700954","04b48b094faeb16683c32669824ebdad","http://sns.whalecloud.com");
//        PlatformConfig.setAlipay("2016123456");
    }


    public static String bg = "green";
    private static App instance;
    private static boolean haveNews;

    public static App getInstance() {
        return instance;
    }

    private List<Activity> activityList = new ArrayList<>();//存放Activity的List集合

    private static DbManager.DaoConfig daoConfig;

    public static DbManager.DaoConfig getDaoConfig() {
        return daoConfig;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        UMShareAPI.get(this);
        instance = this;
        initCloudChannel(this);
        x.Ext.init(this);//Xutils初始化，另外初始化数据库
        x.Ext.setDebug(BuildConfig.DEBUG);
        daoConfig = new DbManager.DaoConfig().setDbName("db_device.db").setDbVersion(1)
                .setDbOpenListener(new DbManager.DbOpenListener() {
                    @Override
                    public void onDbOpened(DbManager db) {
                        // 开启WAL, 对写入加速提升巨大
                        db.getDatabase().enableWriteAheadLogging();
                    }
                })
                .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                    @Override
                    public void onUpgrade(DbManager db, int oldVersion, int newVersion) {

                    }
                });
    }

    /**
     * 表末加入指定的Activity
     *
     * @param activity
     */
    public void addActivity(Activity activity) {
        try {
            if (activity != null && activityList != null) {
                int size = activityList.size();
                if (checkActivityIsVasivle(activity)) {
                    removeActivity(activity);
                    activityList.add(activityList.size(), activity);
                } else {
                    activityList.add(activity);
                }
                size = activityList.size();
                for (int i = 0; i < size; i++) {
//                    Log.i("chen", "addActivity ==[" + i + "]" + " " + activityList.get(i));
                }
            }
        } catch (Exception e) {
//            Log.e("chen", "addActivity" + e.getMessage());
        }

    }

    /**
     * 从表中移除指定activity
     *
     * @param activity
     */
    public void removeActivity(Activity activity) {
        try {
            if (activityList != null) {
                activityList.remove(activity);
            }
        } catch (Exception e) {
        }
    }

    /**
     * 判定某个activity的状态,
     */
    public boolean checkActivityIsVasivle(Activity activity) {
        Log.i("chen", " " + activityList.contains(activity));
        return activityList.contains(activity);
    }

    /**
     * finish all the activity in the list.
     * 干掉所有的Activity用于程序退出
     * the activity calling this method hold the context
     */
    public void finishAllActivity() {
        if (activityList != null) {
            int size = activityList.size();
            for (int i = size - 1; i >= 0; i--) {
                Activity activity = activityList.get(i);
                if (activity != null) {
                    activity.finish();
                }
                Log.i("chen", "finishAllActivity ==[" + i + "]" + " " + activity);
                activityList.remove(activity);
            }
        }
    }

    /**
     * 初始化云推送通道
     *
     * @param context
     */
    private void initCloudChannel(Context context) {
        PushServiceFactory.init(context);
        final CloudPushService pushService = PushServiceFactory.getCloudPushService();
        pushService.register(context, new CommonCallback() {
            @Override
            public void onSuccess(String s) {
                Log.i("chen", "init cloudchannel success");
                FileUtils.bindAccount(pushService);
            }

            @Override
            public void onFailed(String s, String s1) {
                Log.d("chen", "init cloudchannel failed -- errorcode:" + s + " -- errorMessage:" + s1);
            }
        });
    }





    public static boolean isHaveNews() {
        return haveNews;
    }

    public static void setHaveNews(boolean b) {
        haveNews = b;
    }
}
