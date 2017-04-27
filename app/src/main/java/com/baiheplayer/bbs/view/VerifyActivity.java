package com.baiheplayer.bbs.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.baiheplayer.bbs.R;
import com.baiheplayer.bbs.bean.DataBack;
import com.baiheplayer.bbs.bean.local.MsgBack;
import com.baiheplayer.bbs.common.HttpDatas;
import com.baiheplayer.bbs.inter.INetCallBack;
import com.baiheplayer.bbs.inter.ISimpleNetCallBack;
import com.baiheplayer.bbs.utils.CheckChangeImpl;
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
@ContentView(R.layout.activity_verify)
public class VerifyActivity extends BaseActivity {
    @ViewInject(R.id.tv_title)
    private TextView mTitle;
    @ViewInject(R.id.et_register_account)
    private EditText mAccount;             //账号输入
    @ViewInject(R.id.et_register_security)
    private EditText mSecurity;            //验证码输入
    @ViewInject(R.id.tv_register_security)
    private TextView mSendMsg;          //发送验证码按钮
    @ViewInject(R.id.et_register_new_word)
    private EditText mNewWord;
    @ViewInject(R.id.cb_verify_del)
    private CheckBox mLook;



    private Intent intent;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 200) {
                mSendMsg.setText("获取验证码");
                mSendMsg.setEnabled(true);
            }
            if (msg.what == 100){
                Bundle bundle = msg.getData();
                mSendMsg.setText(bundle.getInt("time") + "秒");
            }
        }
    };

    @Override
    public void onView(Bundle b) {
        mSendMsg.setText("获取验证码");
        mLook.setOnCheckedChangeListener(new CheckChangeImpl(mNewWord));
    }

    /**
     * 获取验证码
     * @param view
     */
    @Event(R.id.tv_register_security)
    private void sendSecuritySMS(View view) {
        String account = mAccount.getText().toString().trim();
        if (isAccountLigal(account)) {
            startTimeAttack();
            if (mSendMsg.getText().toString().equals("获取验证码")) {
                RequestParams params = new RequestParams(HttpDatas.SEND_SMS);
                params.addParameter("target", account);
                params.addParameter("sendType", 4);
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

    /**
     * 任何一个手机号或邮箱即可
     * @param str
     * @return
     */
    private boolean isAccountLigal(String str) {
        boolean isLigal = false;
        if ( SaveUtils.isMobileNum(str) || SaveUtils.isEmail(str)) {
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
    @Event(R.id.bt_register_ensure)
    private void bindNewAccount(View view) {
        final String account = mAccount.getText().toString().trim();
        String security = mSecurity.getText().toString().trim();
        String newWord = mNewWord.getText().toString().trim();
        if(TextUtils.isEmpty(security) && TextUtils.isEmpty(newWord)){
            showInfo("有选项漏填了");
        }
        if (!isAccountLigal(account)) {
            showInfo("不是一个有效地账号类型");
            return;
        } else {
            RequestParams params = new RequestParams(HttpDatas.USER_FINDPWD);
            params.addParameter("target", account);
            params.addParameter("code", security);
            params.addParameter("password",newWord);
            HttpsUtils.getInstance().getObjectFromNet(params, DataBack.class, new ISimpleNetCallBack<DataBack>() {
                @Override
                public void back(DataBack resultInfo) {
                    if (resultInfo.getCode() == 0) {
                        showInfo("重置成功");
                        startActivity(new Intent(VerifyActivity.this,LoginActivity.class));
                        finish();
                    }
                }

                @Override
                public void fail(boolean isOnCallBack) {
                    showInfo("网络错误");
                }
            });
        }
    }

    /**
     * 关闭界面
     *
     * @param view
     */
    @Event(value ={R.id.img_back,R.id.img_arrow})
    private void goBack(View view) {
        finish();
    }

    private void showInfo(String info) {
        Snackbar.make(mAccount, info, Snackbar.LENGTH_SHORT).show();
    }
}
