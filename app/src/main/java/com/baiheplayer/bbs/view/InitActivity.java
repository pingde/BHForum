package com.baiheplayer.bbs.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.baiheplayer.bbs.BuildConfig;
import com.baiheplayer.bbs.Constant;
import com.baiheplayer.bbs.R;
import com.baiheplayer.bbs.bean.map.AreaBack;
import com.baiheplayer.bbs.bean.map.Province;

import com.baiheplayer.bbs.bean.local.VersionBack;
import com.baiheplayer.bbs.bean.local.VersionInfo;
import com.baiheplayer.bbs.common.CommonData;
import com.baiheplayer.bbs.common.HttpDatas;
import com.baiheplayer.bbs.inter.ISimpleNetCallBack;
import com.baiheplayer.bbs.inter.ITokenFinish;
import com.baiheplayer.bbs.service.TokenIntentService;
import com.baiheplayer.bbs.ui.LogoView;
import com.baiheplayer.bbs.utils.FileUtils;
import com.baiheplayer.bbs.utils.HttpsUtils;
import com.baiheplayer.bbs.utils.InfoHelper;
import com.baiheplayer.bbs.utils.SaveUtils;
import com.baiheplayer.bbs.utils.Share;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;


/**
 * 闪屏页
 * Token——》 版本检查——》 界面
 * Created by Administrator on 2016/11/14.
 */
@ContentView(R.layout.activity_init)
public class InitActivity extends BaseActivity implements ITokenFinish {
    @ViewInject(R.id.init_background)
    private RelativeLayout mBackground;
    @ViewInject(R.id.lv_logo)
    private LogoView logo;

    private static final int TO_MAIN = 100;
    private static final int TO_NETBOOM = 200;
    private static final int TO_LOGIN = 300;
    private int target = TO_MAIN;
    private long startTime, finishTime;
    private final String ISFIRST = "isFirst";

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TO_MAIN:
                    startActivity(new Intent(InitActivity.this, MainActivity.class));
                    overridePendingTransition(R.anim.anim_alpha_ex, R.anim.anim_alpha);
                    break;
                case TO_LOGIN:
                    startActivity(new Intent(InitActivity.this, LoginActivity.class));
                    overridePendingTransition(R.anim.anim_alpha_ex, R.anim.anim_alpha);
                    break;
                case TO_NETBOOM:
                    startActivity(new Intent(InitActivity.this, NetBoomActivity.class));
                    overridePendingTransition(R.anim.anim_alpha_ex, R.anim.anim_alpha);
                    break;
            }

        }
    };

//    @Override
//    public void onView(DbManager db) {
//        InfoHelper helper = InfoHelper.getInstance();
//        if( !helper.init()){
//            LocalInfo info = new LocalInfo();
//            helper.save(info);
//        }
//        String token = "AB1CD2EF3GH4IG5KL6MN7OP8QR9ST1UV2WX3YZ";
//        App.setAccessToken(token);
//        handler.sendEmptyMessageDelayed(TO_MAIN, 5000);
//    }

    @Override
    public void onView(Bundle savedInstanceState) {
        startService(new Intent().setClass(this, TokenIntentService.class)); //5.0要显性调用
        logo.logoAnimStart(LogoView.TYPE_MESS, true);
        getAppLocation();  //获取经纬度
        startTime = SaveUtils.getCurrentTime();
        Share.init(getApplicationContext());   //初始化Share
        InfoHelper helper = InfoHelper.getInstance();
        helper.init();       //初始化用户信息
        helper.display();    //数据信息转换成常量
        Constant.screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        if (SaveUtils.isNetworkConnected(this)) {
            if (Share.getBoolean(ISFIRST, true)) {
                Log.i("chen","第一次");
                HttpsUtils.getInstance().getToken(this);
            } else {
                Log.i("chen","非第一次");
                HttpsUtils.getInstance().refreshToken(this);
            }
        } else {
            target = TO_NETBOOM;
            goToNextActivity();
        }
    }

    @Override
    public void isOk(Boolean success) {
        if (success) {

            if (Share.getBoolean(ISFIRST, true)) {
                getAreaBackFromNet();  //下载地图
            } else {
                checkVersion();
            }

        } else {
            Snackbar.make(mBackground, "获取失败", Snackbar.LENGTH_SHORT).show();
            goToNextActivity(); // TODO: 2017/4/18 先走着
        }
    }

    /**
     * 下载地图列表
     */
    private void getAreaBackFromNet() {
        Log.i("chen", "下载地图");
        final List<Province> provinces = new ArrayList<>();
        RequestParams params = new RequestParams(HttpDatas.GET_LOCATION);
        HttpsUtils.getInstance().getObjectFromNet(params, AreaBack.class, new ISimpleNetCallBack<AreaBack>() {
            @Override
            public void back(AreaBack resultInfo) {
                if (resultInfo.getCode() == 0) {
                    Share.putBoolean(ISFIRST, false);  //只有地图列表下载成功了才能算完成了第一次打开
                    provinces.addAll(resultInfo.getAreaList());
                    FileUtils.saveData(InitActivity.this, provinces, "map_list.txt");
                    checkVersion();
                }
            }

            @Override
            public void fail(boolean isOnCallBack) {
                Snackbar.make(mBackground, "网络错误", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 获取经纬度
     */
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationoption;

    private void getAppLocation() {
        Log.i("chen", "开始检测地图");
        mLocationoption = new AMapLocationClientOption();
        mLocationClient = new AMapLocationClient(this);
        mLocationoption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationoption.setOnceLocation(true);  //一次定位
        mLocationClient.setLocationOption(mLocationoption);
        mLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation.getErrorCode() == 0) {
                    double lon = aMapLocation.getLongitude();
                    double lat = aMapLocation.getLatitude();
                    mLocationClient.stopLocation();
                    mLocationClient.onDestroy();
                    Log.i("chen", "经度" + lon);
                    Log.i("chen", "纬度" + lat);
                    Constant.longitude = String.valueOf(lon);
                    Constant.latitude = String.valueOf(lat);

                }
            }
        });
        mLocationClient.startLocation();
    }

    /**
     * 版本检查更新
     */
    private void checkVersion() {
        Log.i("chen", "检查版本号");
        RequestParams params = new RequestParams(HttpDatas.VERSION_CHECK);
        params.addParameter("platform", CommonData.PLATFORM);
        params.addParameter("version", BuildConfig.VERSION_NAME);
        HttpsUtils.getInstance().getObjectFromNet(params, VersionBack.class, new ISimpleNetCallBack<VersionBack>() {
            @Override
            public void back(VersionBack resultInfo) {
                if (resultInfo.getCode() == 0) {
                    VersionInfo version = resultInfo.getData();
                    if (version.getNewVersion().equals("0")) {
                        //跳转到主界面
                        goToNextActivity();  //版本正确后，到下一个界面
                    } else {
                        // TODO: 2017/4/17  应该执行这一条弹出操作框
                        showUpdateNotice(version);
//                        goToNextActivity();
                    }
                }
            }

            @Override
            public void fail(boolean isOnCallBack) {
                Snackbar.make(mBackground, "网络错误", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private String updateStr = " 更新信息\n·聊天消息：随时随地收发好友和群消息，一触即达。\n·语音通话：两人、多人语音通话，高清畅聊。\n·视频聊天：亲朋好友，想念不如相见。\n·文件传输：手机、电脑多端互传，方便快捷。\n·空间动态：更快获知好友动态，分享生活留住感动。\n·个性装扮：主题、名片、彩铃、气泡、挂件随心选。\n·游戏中心：天天、全民等热门手游，根本停不下来。\n·移动支付：话费充值、网购、转账收款，一应俱全。\n·QQ运用：同步苹果-健康(HealthKit)运动数据，与好友PK，快乐健康一起玩！\n乐在沟通17年，聊天欢乐9亿人！";

    private void showUpdateNotice(final VersionInfo info) {
        Log.i("chen", "检查版本");
        final AlertDialog.Builder builder = new AlertDialog.Builder(this).setIcon(R.mipmap.ic_launcher)
                .setTitle("发现新版本:" + info.getNewVersion());
        //info.getUpdateInfo()
        View view = View.inflate(this, R.layout.widget_text2, null);
        builder.setView(view);
        TextView tv = (TextView) view.findViewById(R.id.module_text);
        tv.setText(flushUpdateMsg(info.getUpdateInfo()));
        if (!info.isEnforce()) {
            builder.setNegativeButton("暂时不了", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    goToNextActivity();
                }
            });
//            builder.setNeutralButton("忽略此版本", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    //跳转到主界面
//                    localInfo.setIgnoreVersion(info.getNewVersion());
//                    try {
//                        x.getDb(App.getDaoConfig()).update(localInfo);
//                    } catch (DbException e) {
//                        e.printStackTrace();
//                    }
//                    handler.sendEmptyMessage(TO_MAIN);
//                }
//            });
        }

        builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.CATEGORY_APP_BROWSER);
                intent.setAction("android.intent.action.VIEW");
                intent.setData(Uri.parse(info.getUrl()));
                startActivity(intent);
                dialog.dismiss();
                finish();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    private int getSupplyTime(long start, long finish) {
        int supply = 1000;
        if ((finish - start) < 5000) {
            supply = (int) (5000 + start - finish);
        }
        return supply;
    }

    private void goToNextActivity() {
        finishTime = SaveUtils.getCurrentTime();
        handler.sendEmptyMessageDelayed(target, getSupplyTime(startTime, finishTime));
    }

    /**
     * 处理成换行符
     *
     * @param info
     * @return
     */
    private String flushUpdateMsg(String info) {
        String new_info = null;
        new_info = info.replace("\\n", "\n");
        return new_info;
    }

}
