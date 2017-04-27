package com.baiheplayer.bbs.utils;

import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by Administrator on 2016/11/17.
 */

public class DeskFragWebViewClient extends WebViewClient {
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Log.i("WebClient","should load:"+url);
        return super.shouldOverrideUrlLoading(view, url);
    }
    /**
     * 网页开始加载的时候，进行回调的方法，
     * @param view WebView
     * @param url  String
     * @param favicon Bitmap
     */
    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        Log.i("WebClient","should start:"+url);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        Log.i("WebClient","should finish:"+url);
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {

        return super.shouldInterceptRequest(view, request);
    }
}
