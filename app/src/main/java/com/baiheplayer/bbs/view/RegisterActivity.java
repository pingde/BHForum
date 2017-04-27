package com.baiheplayer.bbs.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baiheplayer.bbs.R;
import com.baiheplayer.bbs.bean.DataBack;
import com.baiheplayer.bbs.bean.local.MsgBack;
import com.baiheplayer.bbs.common.HttpDatas;
import com.baiheplayer.bbs.inter.ISimpleNetCallBack;
import com.baiheplayer.bbs.utils.CheckChangeImpl;
import com.baiheplayer.bbs.utils.HttpsUtils;
import com.baiheplayer.bbs.utils.SaveUtils;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

/**
 * 注册界面
 * Created by Administrator on 2016/11/15.
 */
@ContentView(R.layout.activity_register)
public class RegisterActivity extends BaseActivity {
    @ViewInject(R.id.et_sign_account)    //账号
    private EditText mRegisterName;
    @ViewInject(R.id.et_sign_password)   //密码
    private EditText mPassWord;
    @ViewInject(R.id.et_sign_verify)   //验证码
    private EditText mInputSecurity;
    @ViewInject(R.id.tv_get_verify)    //倒计时
    private TextView mSecurity;
    @ViewInject(R.id.cb_register_look)
    private CheckBox mLook;


    //    private LocalInfo localInfo;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 100) { //倒计时
                Bundle bundle = msg.getData();
                mSecurity.setText("" + bundle.getInt("i"));
            }
            if (msg.what == 200) { //刷新倒计时
                mSecurity.setText("获取验证码");
            }
            if (msg.what == 300) {  //注册成功就跑路
                Intent intentToLogin = new Intent(RegisterActivity.this, LoginActivity.class);
                String name = mRegisterName.getText().toString().trim();
                String pwd = mPassWord.getText().toString().trim();
                intentToLogin.putExtra("name", name);
                intentToLogin.putExtra("pwd", pwd);
                startActivity(intentToLogin);
            }

        }
    };

    @Override
    public void onView(Bundle b) {
        mLook.setOnCheckedChangeListener(new CheckChangeImpl(mPassWord));

    }

    /**
     * 发送验证码
     */
    @Event(R.id.tv_get_verify)//发送验证码
    private void getSecurityCode(View view) {
        if ("获取验证码".equals(mSecurity.getText().toString())) {
            // TODO: 2016/11/17 拉取验证码
            String account = mRegisterName.getText().toString().trim();
            if (SaveUtils.isMobileNum(account) || SaveUtils.isEmail(account)) {
                sendHttpToGetSMS();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 59; i >= 0; i--) {
                            Bundle bundle = new Bundle();
                            bundle.putInt("i", i);
                            Message msg = new Message();
                            msg.what = 100;
                            msg.setData(bundle);
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            handler.sendMessage(msg);
                        }
                        handler.sendEmptyMessageDelayed(200, 1000);
                    }
                }).start();
            } else {
                showInfo("可能不是一个有效号码或者邮箱");
                return;
            }

        }
    }

    private void sendHttpToGetSMS() {
        String target = mRegisterName.getText().toString().trim();

        RequestParams params = new RequestParams(HttpDatas.SEND_SMS);
        params.addParameter("target", target);
        params.addParameter("sendType", 2);//1登录，2注册 3重置密码 4找回密码
        HttpsUtils.getInstance().getObjectFromNet(params, MsgBack.class, new ISimpleNetCallBack<MsgBack>() {
            @Override
            public void back(MsgBack resultInfo) {
                if (resultInfo.getCode() == 0) {
                    mInputSecurity.setText(resultInfo.getDataCode());
                }
                showInfo(resultInfo.getCode() == 0 ? "发送成功" : resultInfo.getMsg());
            }

            @Override
            public void fail(boolean isOnCallBack) {
                Snackbar.make(mInputSecurity, "网络错误", Snackbar.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * 已有账号，跳转到登录界面
     *
     * @param view
     */
    @Event(R.id.tv_register_2login)
    private void jump2LoginActivity(View view) {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    /**
     * 关闭界面
     * @param view
     */
    @Event(value = {R.id.rl_back, R.id.ib_back})
    private void goBack(View view) {
        onBackPressed();
    }

    /**
     * 查看服务条款
     *
     * @param view
     */
    @Event(R.id.rule)
    private void jump2SeeServiceRule(View view) {
        startActivity(new Intent(this, SerRuleActivity.class));

    }

    /**
     * 点击注册
     *
     * @param view
     */
    @Event(R.id.btn_sign_ensure)
    private void registerAppCount(View view) {
        String registerName = mRegisterName.getText().toString().trim();
        String registerPassWord = mPassWord.getText().toString().trim();
        String code = mInputSecurity.getText().toString().trim();
        if (TextUtils.isEmpty(registerName)) {
            showInfo("请输入注册账号");
            return;
        }
        if (TextUtils.isEmpty(registerPassWord)) {
            showInfo("请输入账号密码");
            return;
        }

        if (TextUtils.isEmpty(code)) {
            showInfo("请输入验证码");
            return;
        }

        // TODO: 2016/11/25 检验信息的合法性
        if (SaveUtils.isMobileNum(registerName) || SaveUtils.isEmail(registerName)) {
            sendHttpToRegister(registerName, registerPassWord, code);
        } else {
            showInfo("可能不是一个有效号码或者邮箱");
            return;
        }

    }

    private void sendHttpToRegister(String registerName, String registerPassWord, String code) {
        final String mName = registerName.trim();
        RequestParams params = new RequestParams(HttpDatas.USER_REGISTER);
        params.addParameter("target", mName);
        params.addParameter("code", code);
        params.addParameter("password", registerPassWord);
        HttpsUtils.getInstance().getObjectFromNet(params, DataBack.class, new ISimpleNetCallBack<DataBack>() {
            @Override
            public void back(DataBack resultInfo) {
                if (resultInfo.getCode() == 0) {
                    handler.sendEmptyMessageDelayed(300, 1000);
                }
                showInfo(resultInfo.getCode() == 0 ? "注册成功" : resultInfo.getMsg());
            }

            @Override
            public void fail(boolean isOnCallBack) {
                showInfo("网络错误");
            }
        });
    }


    private void showInfo(String info) {
        Snackbar.make(mInputSecurity, info, Snackbar.LENGTH_SHORT).show();
    }

}
