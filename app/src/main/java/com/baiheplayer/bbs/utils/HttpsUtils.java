package com.baiheplayer.bbs.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.baiheplayer.bbs.App;
import com.baiheplayer.bbs.Constant;
import com.baiheplayer.bbs.bean.DataBack;
import com.baiheplayer.bbs.bean.local.TokenBack;
import com.baiheplayer.bbs.bean.LocalInfo;
import com.baiheplayer.bbs.common.CommonData;
import com.baiheplayer.bbs.common.HttpDatas;
import com.baiheplayer.bbs.inter.INetCallBack;
import com.baiheplayer.bbs.inter.ISimpleNetCallBack;
import com.baiheplayer.bbs.inter.ITokenFinish;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by Administrator on 2016/11/22.
 */

public class HttpsUtils {
    private static HttpsUtils httpsUtils;
    private Handler mHandler;
    private Context context;
    private ExecutorService singleThreadExecutor;
    private Gson gson;

    private HttpsUtils() {
        singleThreadExecutor = Executors.newSingleThreadExecutor();
        mHandler = new Handler(Looper.getMainLooper());
        gson = new Gson();
    }

    public static HttpsUtils getInstance() {
        if (httpsUtils == null) {
            httpsUtils = new HttpsUtils();
        }
//        httpsUtils.context = null;
        return httpsUtils;
    }

    public static HttpsUtils getInstance(Context context) {
        if (httpsUtils == null) {
            httpsUtils = new HttpsUtils();
        }
//        httpsUtils.context=context;
        return httpsUtils;
    }

    /**
     * 基本Post网络请求，得到DataBack子类的的封装对象
     *
     * @param params
     * @param clazz
     * @param callback
     * @param <T>
     */
    public <T> void getObjectFromNet(RequestParams params, final Class<T> clazz, final ISimpleNetCallBack<T> callback) {
//        params = SaveUtils.getEncrypt(params); // 加密
        params.addParameter("accessToken", Constant.accessToken);
        Log.i("chen", "url:" + params.getUri() + ", 使用的Token：" + Constant.accessToken);
        params.addParameter("language", "zh_CN");
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                final T t = gson.fromJson(result, clazz);
                if (t instanceof DataBack) {
                    final int code = ((DataBack) t).getCode();
                    final String msg = ((DataBack) t).getMsg();
                    Log.i("chen", code + " " + msg);
                    callback.back(t);


                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i("chen", "onError");
                callback.fail(isOnCallback);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.i("chen", "onCancelled");
            }

            @Override
            public void onFinished() {

            }
        });
    }


    /**
     * 基本的获取Token的特例方法,有回传参数有用的方法
     * 应该只由InfoHelper调用
     *
     * @param callback
     */
    public synchronized void getToken(final ITokenFinish callback) {
        String time = String.valueOf(SaveUtils.getCurrentTime());
        Log.i("chen", "getToken() time" + time);
        String nonce = String.valueOf(CommonData.getRandomNum());
        Log.i("chen", "getToken() nonce" + nonce);
        String sign = CommonData.getMessageDigist(time, nonce);
        Log.i("chen", "getToken() sign" + sign);
        final RequestParams params = new RequestParams(HttpDatas.ACCESS_TOKEN);
        params.addParameter("appid", CommonData.APPID);
        Log.i("chen", "getToken() appid" +  CommonData.APPID);
        params.addParameter("grantType", 1);
        params.addParameter("language", "zh_CN");
        params.addParameter("nonce", nonce);//"zO114t1k"
        params.addParameter("time", time);  // "1477490252571"
        params.addParameter("sign", sign);  //"44c0ae1abd6e50b96d11db7169c7acf9"
        params.addParameter("alicloudPushDevideid", CommonData.alicloudPushDevideid);

//        List<KeyValue> list = params.getQueryStringParams();
//        HashMap<String, Object> map = new HashMap<>();
//        for (int i = 0; i < list.size(); i++) {
//            map.put(list.get(i).key, list.get(i).value);
//        }
//        params.addParameter("data", SaveUtils.encrypt(new Gson().toJson(map), RandomUtil.getCharacterAndNumber(43))); //混淆
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                final TokenBack t = gson.fromJson(result, TokenBack.class);
                Log.i("chen", "LocalInfo" + result);
                InfoHelper helper = InfoHelper.getInstance();
                LocalInfo info = helper.getInfo();
                info.setAccessToken(t.getAccessToken());
                info.setRefreshToken(t.getRefreshToken());
                info.setAeskey(t.getAesKey());
                info.setExpires(t.getExpires());
                boolean isOk = helper.save(info);
                helper.display();  //转化成静态常量
                callback.isOk(isOk);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i("chen", "onError" + "isOnCallback:" + isOnCallback + " Thread:" + Thread.currentThread().getName());
                callback.isOk(false);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.i("chen", "onCancelled");
            }

            @Override
            public void onFinished() {
                Log.i("chen", "onFinished");
            }
        });
    }

/*********************************************************************************************/

    /**
     * 安全请求，请求前看网络状态，另外获取最新的Token
     *
     * @param params
     * @param clazz
     * @param callback
     * @param <T>
     */
    public <T> void requestSavely( final RequestParams params, final Class<T> clazz, final INetCallBack callback) {

//        params = SaveUtils.getEncrypt(params);
        params.addParameter("accessToken", Constant.accessToken);
        Log.i("chen", "url:" + params.getUri() + ", 使用的Token：" + Constant.accessToken);
        params.addParameter("language", "zh_CN");

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                final T t = gson.fromJson(result, clazz);
                if (t instanceof DataBack) {
                    final int code = ((DataBack) t).getCode();
                    final String msg = ((DataBack) t).getMsg();
                    Log.i("chen", code + " " + msg);
                    if (code == 10000 || code == 10002) {
                        // TODO: 2017/4/14 需要刷新Token
                    } else {
                        callback.back(params.getUri(), t);
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i("chen", "onError" + " Thread:" + Thread.currentThread().getName());
                callback.fail(isOnCallback);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.i("chen", "onCancelled");
            }

            @Override
            public void onFinished() {

            }
        });

    }

    /**
     * 刷新Token
     *
     * @param callBack
     */
    public void refreshToken(final ITokenFinish callBack) {

        RequestParams params = new RequestParams(HttpDatas.REFRESH_TOKEN);
        params.addParameter("appid", CommonData.APPID);
        params.addParameter("grantType", 2);
        params.addParameter("language", "zh_CN");
        params.addParameter("refreshToken", Constant.refreshToken);
        Log.i("chen","refreshToken:"+ Constant.refreshToken);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                final TokenBack t = gson.fromJson(result, TokenBack.class);
                Log.i("chen", "HttpUtils的" + t.getCode() + " " + t.getMsg());
                if (t.getCode() == 0) {
                    //拿到信息之后存储
                    InfoHelper helper = InfoHelper.getInstance();
                    LocalInfo info = helper.getInfo();
                    info.setAccessToken(t.getAccessToken());
                    info.setRefreshToken(t.getRefreshToken());
                    info.setAeskey(t.getAesKey());
                    info.setExpires(t.getExpires());
                    boolean isOk = helper.update(info);

                    callBack.isOk(isOk);
                } else  {
                    //重新获取Token
                    getToken(callBack);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i("chen", "刷新Token失败了");
                callBack.isOk(false);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

}
