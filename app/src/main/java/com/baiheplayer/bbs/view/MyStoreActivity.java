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
import com.baiheplayer.bbs.adapter.TopicAdapter;
import com.baiheplayer.bbs.bean.Topic;
import com.baiheplayer.bbs.bean.TopicsBack;
import com.baiheplayer.bbs.common.HttpDatas;
import com.baiheplayer.bbs.common.ImageClickListenerImpl;
import com.baiheplayer.bbs.inter.ISimpleNetCallBack;
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
 * Created by Administrator on 2016/12/7.
 */
@ContentView(R.layout.activity_mystore)
public class MyStoreActivity extends BaseActivity {
    @ViewInject(R.id.ll_notice_head)
    private LinearLayout emptyNotice;
    @ViewInject(R.id.refresh)
    private PullToRefreshScrollView mPullToRefresh;
    @ViewInject(R.id.rv_list)
    private RecyclerView mRecyclerView;

    private int currentPage = 1;
    private int totalPage = 1;

    private List<Topic> topics;
    private TopicAdapter adapter;

    @Override
    public void onView(Bundle b) {

        topics = new ArrayList<>();
        adapter = new TopicAdapter(this, topics);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(adapter);
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
        if(currentPage <= totalPage && currentPage>0){
            RequestParams params = new RequestParams(HttpDatas.MY_STORE);
            params.addParameter("pcurrent", page);
            params.addParameter("psize", 10);
            params.setConnectTimeout(5000);
            HttpsUtils.getInstance().getObjectFromNet(params, TopicsBack.class, new ISimpleNetCallBack<TopicsBack>() {
                @Override
                public void back(TopicsBack resultInfo) {
//                    if(mPullToRefresh.isRefreshing()){
//
//                    }

                    mPullToRefresh.onRefreshComplete();
                    if (resultInfo.getCode() == 0) {
                        currentPage = Integer.parseInt(resultInfo.getCurrent());
                        totalPage = Integer.parseInt(resultInfo.getTotal());
                        topics.addAll(resultInfo.getTopicList());
                        adapter.notifyDataSetChanged();
                    }
                    pullEmptyNotice();
                }

                @Override
                public void fail(boolean isOnCallBack) {
                    mPullToRefresh.onRefreshComplete();
                    showInfo("网络错误");
                }
            });
        }
    }

    /**
     * 无条目提示
     */
    private void pullEmptyNotice() {
        emptyNotice.removeAllViews();   //拿掉无帖显示牌
        if(topics.size() == 0){
            View view = LayoutInflater.from(this).inflate(R.layout.item_empty_view, null);
            TextView msg = (TextView)view.findViewById(R.id.tv_empty_notice);
            msg.setText(" 还没有收藏帖子");
            emptyNotice.addView(view);
        }
    }

    @Event(value = {R.id.img_back,R.id.img_arrow})
    private void goBack(View view) {
        onBackPressed();
    }


    /**
     * 信息提示
     *
     * @param info
     */
    private void showInfo(String info) {
        Snackbar.make(mRecyclerView, info, Snackbar.LENGTH_SHORT).setAction("action", null).show();
    }

}
