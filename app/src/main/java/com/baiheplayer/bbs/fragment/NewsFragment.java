package com.baiheplayer.bbs.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.baiheplayer.bbs.R;
import com.baiheplayer.bbs.adapter.BannerAdapter;
import com.baiheplayer.bbs.bean.Banner;
import com.baiheplayer.bbs.bean.BannerBack;
import com.baiheplayer.bbs.common.HttpDatas;
import com.baiheplayer.bbs.inter.FlushCallback;
import com.baiheplayer.bbs.inter.INetCallBack;
import com.baiheplayer.bbs.ui.SubPullToRefresh;
import com.baiheplayer.bbs.utils.HttpsUtils;
import com.baiheplayer.bbs.utils.SaveUtils;
import com.baiheplayer.bbs.view.NetBoomActivity;
import com.handmark.pulltorefresh.library.PullToRefreshBase;


import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 首页资讯有Banner 子Fragment资讯、 热帖两个
 * Banner 不参与刷新,只在打开界面的时候刷新一回
 * Created by Administrator on 2016/11/11.
 */
@ContentView(R.layout.fragment_news)
public class NewsFragment extends BaseFragment implements INetCallBack {

    @ViewInject(R.id.tabs)
    private TabLayout tabs;
    @ViewInject(R.id.vp_banner_pic)
    private ViewPager mViewPager;
    @ViewInject(R.id.pull_to_refresh)
    private SubPullToRefresh mPullToRefresh;
    @ViewInject(R.id.tv_content_label)
    private TextView mContentLabel;
    @ViewInject(R.id.rg_pointer)
    private RadioGroup mPoints;
    @ViewInject(R.id.banner_container)
    private RelativeLayout mBannerContainer;

    private Context context;
    private List<Banner> banners;   //Banner图的数据源
    private BannerAdapter bannerAdapter;
    private List<Fragment> fragments;
    private ChildFragmentTopic child_topic;
    private ChildFragmentNews child_news;
    private FragmentManager fm;

    private int STATE_ARTICLE = 1;
    private int STATE_TOPIC = 2;
    private int currentState;
    private FlushCallbackImpl fcImpl;
    private boolean isLoadBanner = true;
    private static final int AUTO_RUN = 80;
    private static final int AUTO_FRESH = 81;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case AUTO_RUN:
                    if(mViewPager.getCurrentItem()+1<mViewPager.getChildCount()){
                        mViewPager.setCurrentItem(mViewPager.getCurrentItem()+1,true);
                    } else {
                        mViewPager.setCurrentItem(0);
                    }
                    handler.sendEmptyMessageDelayed(AUTO_RUN,7*1000);
                    break;
                case AUTO_FRESH:
                    refreshBanner();
                    break;
            }
        }
    };

    @Override
    public void getData() {
        this.context = getContext();
        Log.i("chen", "NewsFragment执行oncreat()");
        currentState = STATE_ARTICLE;
    }

    @Override
    public void onView() {
        banners = new LinkedList<>();
        bannerAdapter = new BannerAdapter(context, banners);
        mViewPager.setAdapter(bannerAdapter);

        initChildFragment();
        refreshBanner();
        fcImpl = new FlushCallbackImpl();

    }

    private void refreshBanner(){
        if(isLoadBanner){
            RequestParams params = new RequestParams(HttpDatas.GET_BANNER);
            params.addParameter("position", 10); //10
            HttpsUtils.getInstance().requestSavely( params, BannerBack.class, NewsFragment.this);
        }

    }

    private void initChildFragment() {
        fragments = new ArrayList<>();
        child_news = new ChildFragmentNews();
        child_topic = new ChildFragmentTopic();
        fm = getChildFragmentManager();
        fm.beginTransaction().add(R.id.frame_layout, child_news).show(child_news)
                .add(R.id.frame_layout, child_topic).hide(child_topic).commit();
        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                FragmentTransaction transaction = fm.beginTransaction();
                switch (tab.getPosition()) {
                    case 0:
                        currentState = STATE_ARTICLE;
                        mContentLabel.setText("热点资讯");
                        transaction.show(child_news).hide(child_topic).commit();
                        break;
                    case 1:
                        currentState = STATE_TOPIC;
                        mContentLabel.setText("热帖推荐");
                        transaction.show(child_topic).hide(child_news).commit();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        mPullToRefresh.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefresh.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> pullToRefreshBase) {
                if (!SaveUtils.isNetworkConnected(context)) {
                    context.startActivity(new Intent(context, NetBoomActivity.class));
                    mPullToRefresh.onRefreshComplete();
                    return;
                }

                if (currentState == STATE_ARTICLE) {
                    child_news.refresh(fcImpl);
                }
                if (currentState == STATE_TOPIC) {
                    child_topic.refresh(fcImpl);
                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> pullToRefreshBase) {
                if (!SaveUtils.isNetworkConnected(context)) {
                    context.startActivity(new Intent(context, NetBoomActivity.class));
                    mPullToRefresh.onRefreshComplete();
                    return;
                }
                if (currentState == STATE_ARTICLE) {
                    child_news.loadMore(fcImpl);
                }
                if (currentState == STATE_TOPIC) {
                    child_topic.loadMore(fcImpl);
                }

            }
        });
        mViewPager.setOnPageChangeListener(new PageChangeImpl());
    }

    private void initRadioButton(int num) {
        mPoints.removeAllViews();
        for (int i = 0; i < num; i++) {
            RadioButton rb = new RadioButton(context);
            rb.setHeight(20);
            rb.setWidth(20);
            rb.setButtonDrawable(null);
            rb.setBackgroundResource(R.drawable.selector_point);
            ViewGroup.LayoutParams params = mPoints.getLayoutParams();
            if(i==0){
                rb.setChecked(true);
            }
            mPoints.addView(rb,i,params);
        }
    }

    @Override
    public <T> void back(String type, T resultInfo) {
        if(type.equals(HttpDatas.GET_BANNER)){
            BannerBack data = (BannerBack)resultInfo;
            if (data.getCode() == 0) {

                if(data.getList().size() == 0){
                    mBannerContainer.removeAllViews();
                    isLoadBanner = false ;
                }
                banners.clear();
                banners.addAll(data.getList());
                bannerAdapter.notifyDataSetChanged();
                initRadioButton(banners.size());
            }
            handler.sendEmptyMessageDelayed(AUTO_RUN,7*1000);
            handler.sendEmptyMessageDelayed(AUTO_RUN,10*60*1000);
            mPullToRefresh.onRefreshComplete();
        }
    }

    @Override
    public void fail(boolean isOnCallBack) {
        mPullToRefresh.onRefreshComplete();
        showInfo("网络错误...");
    }



    private class PageChangeImpl implements ViewPager.OnPageChangeListener{
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
        @Override
        public void onPageSelected(int position) {
            for (int i=0;i< mPoints.getChildCount();i++){
                RadioButton rb = (RadioButton)mPoints.getChildAt(i);
                if(position == i){
                    rb.setChecked(true);
                } else {
                    rb.setChecked(false);
                }
            }
        }
        @Override
        public void onPageScrollStateChanged(int state) {}
    }

    /**
     * 刷新结束的回调
     */
    private class FlushCallbackImpl implements FlushCallback {

        @Override
        public void onSuccess() {
            mPullToRefresh.onRefreshComplete();
        }
    }

    /**
     * 信息提示框
     * @param info
     */
    private void showInfo(String info) {
        Snackbar.make(mViewPager, info, Snackbar.LENGTH_SHORT).setAction("action", null).show();
    }
}
