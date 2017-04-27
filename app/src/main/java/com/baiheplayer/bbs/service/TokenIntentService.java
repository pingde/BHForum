package com.baiheplayer.bbs.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.baiheplayer.bbs.App;
import com.baiheplayer.bbs.bean.LocalInfo;
import com.baiheplayer.bbs.bean.TokenBack;
import com.baiheplayer.bbs.common.CommonData;
import com.baiheplayer.bbs.common.HttpDatas;
import com.baiheplayer.bbs.inter.INetCallBack;
import com.baiheplayer.bbs.inter.ISimpleNetCallBack;
import com.baiheplayer.bbs.inter.ITokenFinish;
import com.baiheplayer.bbs.utils.HttpsUtils;
import com.baiheplayer.bbs.utils.SaveUtils;
import com.baiheplayer.bbs.view.MainActivity;
import com.baiheplayer.bbs.view.NetBoomActivity;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * Created by Administrator on 2016/12/19.
 */

public class TokenIntentService extends IntentService {
    private String TAG = "TokenIntentService";
    private boolean isRunning;
    private Context context;
    private SharedPreferences share;
    private DbManager db;
    private LocalInfo localInfo;
    private boolean isOpenApp;
    private int GOTO_MAINACT = 100;
    private int GOTO_NETBOOM = 110;
    private long beginTime,nowTime;
    private Handler handler;

    public TokenIntentService() {
        super(null);
        Log.i("chen", "MyIntentService执行");
    }


    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
//        share = getSharedPreferences("LocalMessage", MODE_PRIVATE);
        isOpenApp = true;
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 100:
                        startActivity(new Intent(getApplicationContext(), NetBoomActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        break;
                    case 200:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        isOpenApp = false;
                        break;
                }
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("chen", "onHandleIntent执行");
        beginTime = SaveUtils.getCurrentTime();
        nowTime = SaveUtils.getCurrentTime();
        int expire = 7200;
        isRunning = true;
        while (isRunning) {         //有了智能判断机制，就不需要检查时间刷新
            if (nowTime >= beginTime + expire * 0.9 * 1000) {
                refreshAccessToken();
                beginTime = SaveUtils.getCurrentTime();
            }
            try {
                Thread.sleep(10 * 60 * 1000);     //每10分钟检查一次
                Log.i("chen", "Service例行检查" + SaveUtils.formatTime("" + nowTime));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            nowTime = SaveUtils.getCurrentTime();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        Log.i(TAG, "onDestroy执行");
    }

    /*************************************************************************/
    private void getAccessToken(final LocalInfo localInfo) {
        HttpsUtils.getInstance().getToken(new ITokenFinish() {
            @Override
            public void isOk(Boolean success) {

            }
        });
    }

    private void refreshAccessToken() {
        HttpsUtils.getInstance().refreshToken(new ITokenFinish() {
            @Override
            public void isOk(Boolean success) {
                Log.i("chen","刷新成功? "+success);
            }
        });
    }




}
