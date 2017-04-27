package com.baiheplayer.bbs.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.baiheplayer.bbs.App;
import com.baiheplayer.bbs.R;
import com.baiheplayer.bbs.adapter.BannerAdapter;
import com.baiheplayer.bbs.adapter.TopicAdapter;
import com.baiheplayer.bbs.bean.Banner;
import com.baiheplayer.bbs.bean.BannerBack;
import com.baiheplayer.bbs.bean.Topic;
import com.baiheplayer.bbs.bean.TopicsBack;
import com.baiheplayer.bbs.bean.LocalInfo;
import com.baiheplayer.bbs.common.HttpDatas;
import com.baiheplayer.bbs.common.ImageClickListenerImpl;
import com.baiheplayer.bbs.inter.INetCallBack;
import com.baiheplayer.bbs.ui.SubPullToRefresh;
import com.baiheplayer.bbs.utils.HttpsUtils;
import com.baiheplayer.bbs.utils.InfoHelper;
import com.baiheplayer.bbs.utils.SaveUtils;
import com.baiheplayer.bbs.view.NetBoomActivity;
import com.baiheplayer.bbs.view.PostActivity;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/15.
 */
@ContentView(R.layout.fragment_forum)
public class ForumFragment extends BaseFragment implements INetCallBack {
    @ViewInject(R.id.refresh)
    private SubPullToRefresh mPullToRefresh;
    @ViewInject(R.id.rv_forum_recycler)
    private RecyclerView mRecyclerView;
    @ViewInject(R.id.banner_container)
    private RelativeLayout mBannerContainer;
    @ViewInject(R.id.vp_banner_pic)
    private ViewPager mViewPager;
    @ViewInject(R.id.rg_pointer)
    private RadioGroup mPoints;

    private Context context;
    private List<Banner> banners;
    private List<Topic> topics;
    private BannerAdapter bannerAdapter;
    private TopicAdapter topicAdapter;
    private int currentPage = 1;
    private int totalPage = 1;
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
        banners = new ArrayList<>();
        topics = new ArrayList<>();

    }

    @Override
    public void onView() {
        final LinearLayoutManager lm = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(lm);
        bannerAdapter = new BannerAdapter(context, banners);
          //打开界面刷新一次
        topicAdapter = new TopicAdapter(context, topics);
        mViewPager.setAdapter(bannerAdapter);
        mViewPager.setOnPageChangeListener(new PageChangeImpl());
        mRecyclerView.setAdapter(topicAdapter);
        refreshBanner();
        refreshData(currentPage);
        mPullToRefresh.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefresh.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> pullToRefreshBase) {
                topics.clear();
                currentPage = 1;
                refreshData(currentPage);
            }
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> pullToRefreshBase) {
                ++currentPage;
                refreshData(currentPage);
            }
        });
    }

    private void refreshData(int page) {
        if (!SaveUtils.isNetworkConnected(context)) {
            context.startActivity(new Intent(context, NetBoomActivity.class));
            mPullToRefresh.onRefreshComplete();
            return;
        }

        if (page <= totalPage && page > 0) {
            /**刷新内容*/
            RequestParams params1 = new RequestParams(HttpDatas.GET_TOPIC);
            params1.addParameter("pcurrent", page);//第几页数据
            params1.addParameter("psize", 10);  //每页数据量
            HttpsUtils.getInstance().requestSavely( params1, TopicsBack.class, ForumFragment.this);
        } else {
            Log.i("chen", "没有更多数据");
            mPullToRefresh.onRefreshComplete();
        }
    }

    private void refreshBanner(){
        /**刷新Banner图*/ //success
        if(isLoadBanner){
            RequestParams params = new RequestParams(HttpDatas.GET_BANNER);
            params.addParameter("position", 10); //20
            HttpsUtils.getInstance().requestSavely(params, BannerBack.class,ForumFragment.this);
        }
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

    /**
     * 发布事件
     * @param view
     */
    @Event(R.id.tv_forum_post)
    private void postTopic(View view) {

        LocalInfo info = InfoHelper.getInstance().getInfo();

        if (TextUtils.isEmpty(info.getUserId())) {
            Snackbar.make(mRecyclerView, "用户未登录", Snackbar.LENGTH_SHORT).show();
        } else {
            startActivity(new Intent(context, PostActivity.class));
        }
    }

    @Override
    public <T> void back(String type, T resultInfo) {
        if(type.equals(HttpDatas.GET_TOPIC)){
            TopicsBack data = (TopicsBack)resultInfo;
            if (data.getCode() == 0) {
                totalPage = Integer.valueOf(data.getTotal());
                currentPage = Integer.valueOf(data.getCurrent());
                topics.addAll(data.getTopicList());
                topicAdapter.notifyDataSetChanged();
            }
            mPullToRefresh.onRefreshComplete();

        }

        if(type.equals(HttpDatas.GET_BANNER)){
            BannerBack data = (BannerBack)resultInfo;
            if(data.getCode() == 0){
                if(data.getList().size() == 0){
                    mBannerContainer.removeAllViews();
                    isLoadBanner = false;
                }
                banners.clear();
                banners.addAll(data.getList());
                bannerAdapter.notifyDataSetChanged();
            }
            initRadioButton(banners.size());
            handler.sendEmptyMessageDelayed(AUTO_RUN,7*1000);
            handler.sendEmptyMessageDelayed(AUTO_FRESH,60*10*1000); //十分钟后刷新一次

        }

    }

    @Override
    public void fail(boolean isOnCallBack) {
        Snackbar.make(mRecyclerView,"网络错误",Snackbar.LENGTH_SHORT).show();
        mPullToRefresh.onRefreshComplete();
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



}
