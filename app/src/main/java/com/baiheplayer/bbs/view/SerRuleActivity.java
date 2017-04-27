package com.baiheplayer.bbs.view;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.baiheplayer.bbs.R;
import com.baiheplayer.bbs.utils.DeskFragWebChormeClient;
import com.baiheplayer.bbs.utils.DeskFragWebViewClient;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by Administrator on 2017/2/7.
 */
@ContentView(R.layout.activity_serrule)
public class SerRuleActivity extends BaseActivity {
    @ViewInject(R.id.web)
    private WebView mWeb;
    @Override
    public void onView(Bundle b) {
        mWeb.setWebViewClient(new DeskFragWebViewClient());
        mWeb.setWebChromeClient(new DeskFragWebChormeClient());
        WebSettings setting = mWeb.getSettings();
        setting.setJavaScriptEnabled(true);
        setting.setJavaScriptCanOpenWindowsAutomatically(true);
        setting.setAllowFileAccess(true);
//        mWeb.loadUrl("https://www.baidu.com");
        mWeb.loadUrl("file:///android_asset/rule/rule.html");

    }

    @Event(value = {R.id.img_back,R.id.img_arrow})
    private void goBack(View view){
        finish();
    }
}
