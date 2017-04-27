package com.baiheplayer.bbs.view;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baiheplayer.bbs.R;

import org.xutils.DbManager;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by Administrator on 2017/2/16.
 */
@ContentView(R.layout.activity_net)
public class NetJSActivity extends BaseActivity {

    @ViewInject(R.id.tv_title)
    private TextView mTitle;
    @ViewInject(R.id.web)
    private WebView mContent;
    @ViewInject(R.id.progressBar)
    private ProgressBar mProgress;

    @Override
    public void onView(Bundle b) {
        String title = getIntent().getStringExtra("title");
        if (TextUtils.isEmpty(title)) {
            mTitle.setText("详情");
        } else {
            mTitle.setText(title);
        }
        mTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        mTitle.setSingleLine(true);
        mTitle.setMarqueeRepeatLimit(10);
        String url = getIntent().getStringExtra("url");
        mContent.setWebChromeClient(new WebChromeImpl());
        mContent.setWebViewClient(new WebViewImpl());
        WebSettings setting = mContent.getSettings();
        setting.setJavaScriptEnabled(true);
        mContent.loadUrl(url);  //url
    }


    @Event(value = {R.id.img_arrow, R.id.img_back})
    private void goback(View v) {
        finish();
    }

    @Override
    public void onBackPressed() {
        if(mContent.canGoBack()){
            mContent.goBack();
        } else {
            super.onBackPressed();
        }

    }

    private class WebChromeImpl extends WebChromeClient {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            mTitle.setText(title);
            super.onReceivedTitle(view, title);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                mProgress.setVisibility(View.INVISIBLE);
            } else {
                if (View.INVISIBLE == mProgress.getVisibility()) {
                    mProgress.setVisibility(View.VISIBLE);
                } else {
                    mProgress.setProgress(newProgress);
                }
            }
            super.onProgressChanged(view, newProgress);
        }
    }

    private class WebViewImpl extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
//            return true;
        }
    }
}
