package com.baiheplayer.bbs.fragment;

import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.baiheplayer.bbs.R;
import com.baiheplayer.bbs.utils.DeskFragWebChormeClient;
import com.baiheplayer.bbs.utils.DeskFragWebViewClient;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshWebView;

import org.xutils.DbManager;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

/**
 * 桌子App
 * Created by Administrator on 2016/11/15.
 */
@ContentView(R.layout.fragment_desk)
public class DeskFragment extends BaseFragment {

    private static DeskFragment deskFragment;
    public static DeskFragment getDeskFragment(){
        if(deskFragment == null){
            deskFragment = new DeskFragment();
        }
        return deskFragment;
    }
    @ViewInject(R.id.wv_desk_web)
    private WebView mWeb;
    @ViewInject(R.id.ptrw_web_view)
    private PullToRefreshWebView mPullToRefresh;

    @Override
    public void getData() {
        Log.i("chen","DeskFragment执行oncreat()");
    }

    @Override
    public void onView() {
        mWeb.setWebViewClient(new DeskFragWebViewClient());
        mWeb.setWebChromeClient(new DeskFragWebChormeClient());
        WebSettings setting = mWeb.getSettings();
        setting.setJavaScriptEnabled(true);
        setting.setJavaScriptCanOpenWindowsAutomatically(true);
        setting.setAllowFileAccess(true);
//        mWeb.loadUrl("https://www.baidu.com");
        mWeb.loadUrl("file:///android_asset/html/confirm_receipt.html");

        mPullToRefresh.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<WebView>() {
            @Override
            public void onRefresh(PullToRefreshBase<WebView> pullToRefreshBase) {
                Toast.makeText(getContext(),"下拉",Toast.LENGTH_SHORT).show();
                mWeb.loadUrl("file:///android_asset/html/confirm_receipt.html");
                mPullToRefresh.onRefreshComplete();
            }
        });
    }


}
