package com.baiheplayer.bbs.view;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.baiheplayer.bbs.R;
import com.baiheplayer.bbs.adapter.MyTopicAdapter;
import com.baiheplayer.bbs.bean.Topic;
import com.baiheplayer.bbs.bean.TopicsBack;
import com.baiheplayer.bbs.common.HttpDatas;
import com.baiheplayer.bbs.inter.INetCallBack;
import com.baiheplayer.bbs.inter.IRefreshListener;
import com.baiheplayer.bbs.utils.HttpsUtils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的帖子
 * Created by Administrator on 2016/12/1.
 */
@ContentView(R.layout.activity_notice)
public class MyTopicActivity extends BaseActivity implements INetCallBack {
    @ViewInject(R.id.pull_to_refresh)
    private PullToRefreshScrollView mPullToRefresh;
    @ViewInject(R.id.rv_list)
    private RecyclerView mRecyclerView;
    @ViewInject(R.id.ll_notice_head)
    private LinearLayout emptyNotice;


    private List<Topic> topics;
    private MyTopicAdapter myTopicAdapter;
    private int currentPage = 1;
    private int totalPage = 1;

    @Override
    public void onView(Bundle b) {

        topics = new ArrayList<>();  //数据集合
        myTopicAdapter = new MyTopicAdapter(this,topics); //适配
        myTopicAdapter.addRefreListener(new RefreshListenerImpl());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(myTopicAdapter);
        refreshData(currentPage);
        mPullToRefresh.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefresh.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> pullToRefreshBase) {
                topics.clear();
                currentPage =1;
                refreshData(currentPage);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> pullToRefreshBase) {
                refreshData(currentPage+1);
            }
        });
       
    }

    private void refreshData(int page) {
        if (page <= totalPage && page > 0) {
            RequestParams params = new RequestParams(HttpDatas.MY_LIST);
            params.addParameter("pcurrent", page);//可选页码
            params.addParameter("psize", 10);//可选数据量  是否设置缓存
            HttpsUtils.getInstance().requestSavely(params, TopicsBack.class,MyTopicActivity.this);
        }
        mPullToRefresh.onRefreshComplete();
    }

    private void pullEmptyNotice() {
        emptyNotice.removeAllViews();   //拿掉无帖显示牌
        if(topics.size() == 0){
            View view = LayoutInflater.from(this).inflate(R.layout.item_empty_view, null);
            TextView msg = (TextView)view.findViewById(R.id.tv_empty_notice);
            msg.setText(" 还没有发布帖子");
            emptyNotice.addView(view);
        }
    }


    @Event(value = {R.id.img_back,R.id.img_arrow})
    private void goBack(View view) {
        finish();
    }

    private void showInfo(String info) {
        Snackbar.make(mRecyclerView, info, Snackbar.LENGTH_SHORT).setAction("action", null).show();
    }

    @Override
    public <T> void back(String type, T resultInfo) {
        TopicsBack data = (TopicsBack)resultInfo;
        if(data.getCode() == 0){
            currentPage = Integer.parseInt(data.getCurrent());
            totalPage = Integer.parseInt(data.getTotal());
            topics.addAll(data.getTopicList());
            myTopicAdapter.notifyDataSetChanged();
        }
        pullEmptyNotice();
        mPullToRefresh.onRefreshComplete();

    }

    @Override
    public void fail(boolean isOnCallBack) {
        pullEmptyNotice();
        mPullToRefresh.onRefreshComplete();
        showInfo("网络错误");
    }

    /**
     * 刷新一下
     */
    private class RefreshListenerImpl implements IRefreshListener{

        @Override
        public void onRefreshComplete(String id) {
            topics.clear();
            currentPage =1;
            refreshData(currentPage);
        }
    }
}
