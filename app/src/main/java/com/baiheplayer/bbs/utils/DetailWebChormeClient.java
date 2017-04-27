package com.baiheplayer.bbs.utils;

import android.app.Activity;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

/**
 * Created by Administrator on 2016/11/17.
 */

public class DetailWebChormeClient extends WebChromeClient {

    private Activity activity;

    public DetailWebChormeClient() {
    }

    public DetailWebChormeClient(Activity activity){
        this.activity = activity;
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);
        if (activity != null) {
            activity.setTitle(title);
        }
        super.onReceivedTitle(view, title);
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        if (activity != null) {
            //activity的进度是0-10000
            activity.setProgress(newProgress * 100);
        }
        super.onProgressChanged(view, newProgress);
    }
}
