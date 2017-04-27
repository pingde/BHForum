package com.baiheplayer.bbs.view;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.baiheplayer.bbs.R;
import com.baiheplayer.bbs.adapter.MyNewsAdapter;
import com.baiheplayer.bbs.bean.MyNews;
import com.baiheplayer.bbs.bean.MyNewsBack;
import com.baiheplayer.bbs.common.HttpDatas;
import com.baiheplayer.bbs.inter.ISimpleNetCallBack;
import com.baiheplayer.bbs.utils.HttpsUtils;
import com.baiheplayer.bbs.utils.SaveUtils;
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
 * Created by Administrator on 2017/2/10.
 */
@ContentView(R.layout.activity_notice)
public class MyNewsActivity extends BaseActivity {
    @ViewInject(R.id.tv_title)
    private TextView mTitle;
    @ViewInject(R.id.pull_to_refresh)
    private PullToRefreshScrollView mPullToRefresh;
    @ViewInject(R.id.rv_list)
    private RecyclerView mRecyclerView;
    @ViewInject(R.id.ll_notice_head)
    private LinearLayout emptyNotice;


    private int currentPage = 1;
    private int totalPage = 1;
    private long lastFreshTime = 0;
    private List<MyNews> myNewses;
    private MyNewsAdapter adapter;

    @Override
    public void onView(Bundle b) {

        mTitle.setText("我的消息");
        myNewses = new ArrayList<>();
        adapter = new MyNewsAdapter(this, myNewses);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(adapter);
        mPullToRefresh.setMode(PullToRefreshBase.Mode.BOTH);
        refreshData();
        mPullToRefresh.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> pullToRefreshBase) {
                myNewses.clear();
                lastFreshTime = 0;
                refreshData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> pullToRefreshBase) {
                refreshData();
            }
        });
    }

    @Event(value = {R.id.img_back, R.id.img_arrow})
    private void goBack(View v) {
        finish();
    }

    private void initWidget() {

    }


    private void refreshData() {
        if(!SaveUtils.isNetworkConnected(this)){
            mPullToRefresh.onRefreshComplete();
            return;
        }
        RequestParams params = new RequestParams(HttpDatas.MY_NEWS);
        params.addParameter("time", lastFreshTime);
        HttpsUtils.getInstance().getObjectFromNet( params, MyNewsBack.class, new ISimpleNetCallBack<MyNewsBack>() {
            @Override
            public void back(MyNewsBack resultInfo) {
                if (resultInfo.getCode() == 0) {
                    List<MyNews> temp = resultInfo.getList();
                    getLastFreshTime(temp);
                    myNewses.addAll(temp);
                    adapter.notifyDataSetChanged();
                    pullEmptyNotice();
                    mPullToRefresh.onRefreshComplete();
                }
            }

            @Override
            public void fail(boolean isOnCallBack) {
                Snackbar.make(mRecyclerView,"网络错误",Snackbar.LENGTH_SHORT).show();
            }
        });
    }


    private void pullEmptyNotice() {
        emptyNotice.removeAllViews();   //拿掉无帖显示牌
        if (myNewses.size() == 0) {
            View view = LayoutInflater.from(this).inflate(R.layout.item_empty_view, null);
            TextView msg = (TextView) view.findViewById(R.id.tv_empty_notice);
            msg.setText("您还没有收到消息");
            emptyNotice.addView(view);
        }
    }

    private void getLastFreshTime(List<MyNews> list) {
        int size = list.size();
        if (size > 0) {
            lastFreshTime = list.get(size-1).getCreateTime();
        } else {
            lastFreshTime = 0;
        }
    }
}
