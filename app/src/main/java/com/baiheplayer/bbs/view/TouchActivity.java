package com.baiheplayer.bbs.view;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.baiheplayer.bbs.R;
import com.baiheplayer.bbs.common.CommonData;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;

/**
 * Created by Administrator on 2017/2/7.
 */
@ContentView(R.layout.activity_touch_us)
public class TouchActivity extends BaseActivity {
    @Override
    public void onView(Bundle b) {

    }

    /**
     * 回退
     * @param v
     */
    @Event(value = {R.id.img_back,R.id.img_arrow})
    private void goBack(View v){
        onBackPressed();
    }

    @Event(R.id.touch_net)
    private void goToOfficalNet(View v){

        Intent intent = new Intent(Intent.CATEGORY_APP_BROWSER);
        intent.setAction("android.intent.action.VIEW");
        intent.setData(Uri.parse(CommonData.OFFICIAL_WEB));
        startActivity(intent);
    }

    @Event(R.id.touch_we)
    private void goToWechat(View v){

    }

    @Event(R.id.touch_qq)
    private void goToQQServicer(View v){
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(CommonData.QQ_SERVICE)));
    }

    @Event(R.id.touch_email)
    private void goToCorpEmail(View v){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, CommonData.CORP_EMAIL);
        intent.putExtra(Intent.EXTRA_SUBJECT, "github");    //账号名字
//        intent.putExtra(Intent.EXTRA_TEXT, "coding for fun!");

        try {
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "未安装邮箱应用！", Toast.LENGTH_SHORT).show();
        }
    }

    @Event(R.id.touch_call)
    private void goToPhoneCall(View v){
      startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(CommonData.HOT_CALL)));
    }

}
