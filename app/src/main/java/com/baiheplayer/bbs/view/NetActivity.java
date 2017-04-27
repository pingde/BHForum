package com.baiheplayer.bbs.view;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.baiheplayer.bbs.R;
import com.baiheplayer.bbs.ui.SubPullToRefresh;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.onegravity.rteditor.RTEditText;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

/**
 * 浏览网络资讯的界面
 * Created by Administrator on 2017/2/15.
 */
@ContentView(R.layout.activity_net)
public class NetActivity extends BaseActivity {
//    @ViewInject(R.id.tv_title)
//    private TextView mTitle;
    @ViewInject(R.id.web)
    private WebView mContent;
    @ViewInject(R.id.progressBar)
    private ProgressBar mProgress;
    @ViewInject(R.id.rtEditText)
    private RTEditText mText;
    @ViewInject(R.id.pull_to_refresh)
    private SubPullToRefresh mPullToRefresh;

    @Override
    public void onView(Bundle b ) {
        String title = getIntent().getStringExtra("title");

        final String url = getIntent().getStringExtra("url");
        WebSettings setting = mContent.getSettings();
//        setting.setJavaScriptEnabled(true);
        setting.setSupportZoom(true);
        mContent.setWebChromeClient(new WebChromeImpl());
        mContent.setWebViewClient(new WebViewImpl());
        mContent.addJavascriptInterface(new JavascriptInterface(),"imagelistner");
        mContent.loadUrl(url);
        Log.i("chen",url);
        mProgress.setMax(100);
        mContent.setClickable(false);
        mPullToRefresh.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        mPullToRefresh.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> pullToRefreshBase) {
                mContent.loadUrl(url);
                mPullToRefresh.onRefreshComplete();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        mContent.stopLoading();
    }

    @Event(value = {R.id.img_arrow, R.id.img_back})
    private void goback(View v) {
       onBackPressed();
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
            Log.i("chen","newProgress："+newProgress);
            super.onProgressChanged(view, newProgress);
        }

    }


    /**
     * 定义WebViewClient
     */
    private class WebViewImpl extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            Log.i("chen",request.toString());
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // html加载完成之后，添加监听图片的点击js函数
            // 这段js函数的功能就是，遍历所有的img几点，并添加onclick函数，函数的功能是在图片点击的时候调用本地java接口并传递url过去
            mContent.loadUrl("javascript:(function(){" +
                    "var objs = document.getElementsByTagName(\"img\"); " +
                    "for(var i=0;i<objs.length;i++)  " +
                    "{"
                    + "    objs[i].onclick=function()  " +
                    "    {  "
                    + "        window.imagelistner.openImage(this.src);  " +
                    "    }  " +
                    "}" +
                    "})()");
            mContent.stopLoading();
        }


    }

    private class JavascriptInterface{
        @android.webkit.JavascriptInterface
        public void openImage(String img){
//            System.out.println(img);
//            Intent intent = new Intent();
//            intent.putExtra("image", img);
//            intent.setClass(NetActivity.this, Picture1Activity.class);
//            startActivity(intent);
//            System.out.println(img);
        }
    }
}
