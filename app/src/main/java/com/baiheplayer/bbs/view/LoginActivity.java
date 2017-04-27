package com.baiheplayer.bbs.view;


import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.baiheplayer.bbs.Constant;
import com.baiheplayer.bbs.R;
import com.baiheplayer.bbs.bean.LocalInfo;
import com.baiheplayer.bbs.bean.local.UserBack;
import com.baiheplayer.bbs.common.HttpDatas;
import com.baiheplayer.bbs.inter.ISimpleNetCallBack;
import com.baiheplayer.bbs.utils.CheckChangeImpl;
import com.baiheplayer.bbs.utils.HttpsUtils;
import com.baiheplayer.bbs.utils.InfoHelper;
import com.baiheplayer.bbs.utils.Share;

import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * 登录界面
 * Created by Administrator on 2016/11/15.
 */
@ContentView(R.layout.activity_login)
public class LoginActivity extends BaseActivity {
    @ViewInject(R.id.et_login_name)
    private EditText mInputName;          //账号
    @ViewInject(R.id.et_login_secret)
    private EditText mInputPassWord;      //密码
    @ViewInject(R.id.cb_login_remember)
    private CheckBox mRemember;           //记录
    @ViewInject(R.id.cb_login_look)
    private CheckBox mLook;

    private WifiManager wifiManager;
    private final String ISREM = "isRemember";
    private final String ISLOGIN = "isLogin";
    private final String ACCOUNT = "account";
    private final String PASSWORD = "password";

    @Override
    public void onView(Bundle db) {

        mRemember.setChecked(Share.getBoolean(ISREM,false));  //设置CheckBox的状态
        LoginDifferentWay();
        wifiManager = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);
        mLook.setOnCheckedChangeListener(new CheckChangeImpl(mInputPassWord));
    }

    private void LoginDifferentWay() {
        Intent intentFromOther = getIntent();
        String name = intentFromOther.getStringExtra("name");
        String pwd = intentFromOther.getStringExtra("pwd");
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(pwd)) { //带有账号密码而来
            mInputName.setText(name);
            mInputPassWord.setText(pwd);
        } else if (!TextUtils.isEmpty(name)) {  //带有账号而来
            mInputName.setText(name);
        } else {     //空intent而来
            if (mRemember.isChecked()) {
                mInputName.setText(Share.getString(ACCOUNT,""));
                mInputPassWord.setText(Share.getString(PASSWORD,""));
            }
        }
    }

    @Event(value = {R.id.ib_back,R.id.rl_back})
    private void goBack(View view){
        onBackPressed();
    }


    /**
     * 没有账号 转注册界面
     */
    @Event(R.id.tv_login_2register)
    private void jump2RegisterActivity(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    @Event(R.id.tv_login_forget)
    private void jump2RegisterByPhone(View view) {
        startActivity(new Intent(this,VerifyActivity.class));

    }

    /**
     * 登录
     */
    @Event(R.id.btn_login_ensure)
    private void loginApp(View view) {
        String userName = mInputName.getText().toString().trim();
        String userPassWord = mInputPassWord.getText().toString().trim();
        if(TextUtils.isEmpty(userName)){
            showInfo("账号未填");
            return;
        }

        if (TextUtils.isEmpty(userPassWord)) {
            showInfo("密码未填");
            return;
        }
        // TODO: 2016/11/17  校验登录信息
        sendHttpLogin(userName, userPassWord);
    }

    private void sendHttpLogin(String user, String pwd) {

        boolean isWifi = wifiManager.isWifiEnabled();
        RequestParams params = new RequestParams(HttpDatas.USER_LOGIN);
        params.addParameter("loginAccount", user);
        params.addParameter("loginPassword", pwd);
        params.addParameter("lng", Constant.longitude);   //lng
        params.addParameter("lat", Constant.latitude);    //lat
        params.addParameter("wifi", isWifi);                    //isWifi
        HttpsUtils.getInstance().getObjectFromNet(params, UserBack.class, new ISimpleNetCallBack<UserBack>() {
                @Override
                public void back(UserBack resultInfo) {
                    if (resultInfo.getCode() == 0) {
                        saveLoginBackMsg(resultInfo);
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    } else {
                        showInfo(resultInfo.getMsg());
                    }
                }

                @Override
                public void fail(boolean isOnCallBack) {
                    showInfo("网络错误");
                }
            });
    }

    private void saveLoginBackMsg(final UserBack resultInfo) {
        x.task().run(new Runnable() {
            @Override
            public void run() {
                InfoHelper helper = InfoHelper.getInstance();
                LocalInfo info = helper.getInfo();
                info.setNickname(resultInfo.getNickname());
                info.setAvatar(resultInfo.getAvatar());
                info.setPhone(resultInfo.getPhone());
                info.setEmail(resultInfo.getEmail());
                info.setUserId(resultInfo.getUserId());
                info.setSignature(resultInfo.getSignature());
                info.setAddressFull(resultInfo.getAddressFull());
                info.setLogState(true);
                boolean success = helper.update(info);
                if(success){
                    Log.i("chen","登录信息保存成功");
                    helper.display();
                }

                if(mRemember.isChecked()){
                    String account = mInputName.getText().toString().trim();
                    String password = mInputPassWord.getText().toString().trim();
                    Share.putBoolean(ISREM,true);
                    Share.putString(ACCOUNT,account);
                    Share.putString(PASSWORD,password);
                } else {
                    Share.putBoolean(ISREM,false);
                }
                Share.putBoolean(ISLOGIN,true);
            }
        });

    }

    private void showInfo(String info) {
        Snackbar.make(mInputName, info, Snackbar.LENGTH_SHORT).setAction("action", null).show();
    }
}
