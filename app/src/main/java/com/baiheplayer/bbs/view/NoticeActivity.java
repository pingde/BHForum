package com.baiheplayer.bbs.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.baiheplayer.bbs.R;
import com.baiheplayer.bbs.adapter.TopicAdapter;
import com.baiheplayer.bbs.bean.Topic;
import com.baiheplayer.bbs.common.ImageClickListenerImpl;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;


/**
 * 展示通知、个人帖子、消息等的页面
 * Created by Administrator on 2016/11/14.
 */
@ContentView(R.layout.activity_notice)
public class NoticeActivity extends BaseActivity {

    @ViewInject(R.id.pull_to_refresh) private PullToRefreshScrollView mPullToRefresh;
    @ViewInject(R.id.tv_title) private TextView mTitle;
    @ViewInject(R.id.rv_list) private RecyclerView mRecyclerView;
    @ViewInject(R.id.ll_notice_head) private LinearLayout emptyNotice;

    private TopicAdapter adapter;

    private List<Topic> mList = new ArrayList<>();

    public void onView(Bundle b) {
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        // TODO: 2017/4/21 通知没有
        mTitle.setText(name);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TopicAdapter(this,mList);
        mRecyclerView.setAdapter(adapter);
        pullEmptyNotice();
        mPullToRefresh.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefresh.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> pullToRefreshBase) {
                mList.clear();
                adapter.notifyDataSetChanged();
                mPullToRefresh.onRefreshComplete();
                pullEmptyNotice();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> pullToRefreshBase) {
                adapter.notifyDataSetChanged();
                mPullToRefresh.onRefreshComplete();
            }
        });
    }

    /**
     * 无条目提示
     */
    private void pullEmptyNotice() {
        emptyNotice.removeAllViews();   //拿掉无帖显示牌
        if(mList.size() == 0){
            View view = LayoutInflater.from(this).inflate(R.layout.item_empty_view, null);
            TextView msg = (TextView)view.findViewById(R.id.tv_empty_notice);
            msg.setText("还没有通知");
            emptyNotice.addView(view);  //view 最后垃圾界面没有被回收
        }
    }


    @Event(value = {R.id.img_back,R.id.img_arrow})
    private void closeActivity(View view){
        onBackPressed();
    }

}
