package com.baiheplayer.bbs.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.baiheplayer.bbs.R;
import com.baiheplayer.bbs.bean.DataBack;
import com.baiheplayer.bbs.bean.local.MsgBack;
import com.baiheplayer.bbs.common.HttpDatas;
import com.baiheplayer.bbs.inter.ISimpleNetCallBack;
import com.baiheplayer.bbs.utils.HttpsUtils;
import com.baiheplayer.bbs.utils.SaveUtils;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by Administrator on 2016/12/15.
 */
@ContentView(R.layout.activity_reset)
public class ResetActivity extends BaseActivity {
    @ViewInject(R.id.tv_title)
    private TextView mTitle;             //标题
    @ViewInject(R.id.et_register_name)
    private EditText mAccount;             //账号
    @ViewInject(R.id.et_reset_security)
    private EditText mSecurity;                 //验证码
    @ViewInject(R.id.tv_reset_security)
    private TextView mSendMsg;                  //发送验证码


    private Intent intent;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 200) {
                mSendMsg.setText("获取验证码");
                mSendMsg.setEnabled(true);
            }
            if (msg.what == 100) {
                Bundle bundle = msg.getData();
                mSendMsg.setText(bundle.getInt("time") + "秒");
            }
        }
    };

    @Override
    public void onView(Bundle b) {

        intent = getIntent();
        String type = intent.getStringExtra("title");
        mTitle.setText("绑定" + type);
        mSendMsg.setText("获取验证码");
        mAccount.setHint(type + "账号");
    }

    /**
     * 获取验证码
     *
     * @param view
     */
    @Event(R.id.tv_reset_security)
    private void sendSecuritySMS(View view) {
        String account = mAccount.getText().toString().trim();
        if (isAccountLigal(account)) {
            startTimeAttack();
//            String targetType = mTitle.getText().toString().trim().contains("邮箱")? "邮箱地址":"手机号";
            if (mSendMsg.getText().toString().equals("获取验证码")) {
                RequestParams params = new RequestParams(HttpDatas.SEND_SMS);
                params.addParameter("target", account);
                params.addParameter("sendType", 5);
                HttpsUtils.getInstance().getObjectFromNet(params, MsgBack.class, new ISimpleNetCallBack<MsgBack>() {
                    @Override
                    public void back(MsgBack resultInfo) {
                        if (resultInfo.getCode() == 0) {
                            // TODO: 2016/12/15  测试直接拿取验证码
                            mSecurity.setText(resultInfo.getDataCode());
                        }
                    }

                    @Override
                    public void fail(boolean isOnCallBack) {
                        showInfo("网络错误");
                    }
                });
            }
        } else {
            showInfo("可能不是有效账号");
        }


    }

    private boolean isAccountLigal(String str) {
        boolean isLigal = false;
        if (mTitle.getText().toString().contains("邮箱") && SaveUtils.isEmail(str)) {
            isLigal = true;
        }
        if (mTitle.getText().toString().contains("手机") && SaveUtils.isMobileNum(str)) {
            isLigal = true;
        }
        return isLigal;
    }

    private void startTimeAttack() {
        mSendMsg.setEnabled(false);
        new Thread() {
            @Override
            public void run() {
                super.run();
                for (int i = 59; i > 0; i--) {
                    Message msg = new Message();
                    msg.what = 100;
                    Bundle args = new Bundle();
                    args.putInt("time", i);
                    msg.setData(args);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handler.sendMessage(msg);
                }
                handler.sendEmptyMessageDelayed(200, 1000);
            }
        }.start();
    }

    /**
     * 发送更改请求
     */
    @Event(R.id.img_register_ensure)
    private void bindNewAccount(View view) {
        final String account = mAccount.getText().toString().trim();
        String security = mSecurity.getText().toString().trim();
        if (!isAccountLigal(account) || !TextUtils.isEmpty(security)) {
            showInfo("可能不是一个有效账号");
        }
        RequestParams params = new RequestParams(HttpDatas.BIND_ACCOUNT);
        params.addParameter("target", account);
        params.addParameter("code", security);
        HttpsUtils.getInstance().getObjectFromNet(params, DataBack.class, new ISimpleNetCallBack<DataBack>() {
            @Override
            public void back(DataBack resultInfo) {
                if (resultInfo.getCode() == 0) {

                    onBackPressed();
                    intent.putExtra("newContent", account);
                    setResult(Activity.RESULT_OK, intent);
                }
                showInfo(resultInfo.getCode() == 0?"绑定成功":resultInfo.getMsg());
            }

            @Override
            public void fail(boolean isOnCallBack) {
                showInfo("网络错误");
            }
        });
    }

    /**
     * 关闭界面
     *
     * @param view
     */
    @Event(value = {R.id.img_back, R.id.img_arrow})
    private void goBack(View view) {
        onBackPressed();
    }

    private void showInfo(String info) {
        Snackbar.make(mAccount, info, Snackbar.LENGTH_SHORT).show();
    }
}
