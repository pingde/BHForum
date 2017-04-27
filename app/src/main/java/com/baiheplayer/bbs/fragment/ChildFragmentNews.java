package com.baiheplayer.bbs.fragment;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.baiheplayer.bbs.R;
import com.baiheplayer.bbs.adapter.ArticleAdapter;
import com.baiheplayer.bbs.bean.Article;
import com.baiheplayer.bbs.bean.ArticleBack;
import com.baiheplayer.bbs.common.HttpDatas;
import com.baiheplayer.bbs.common.ImageClickListenerImpl;
import com.baiheplayer.bbs.inter.FlushCallback;
import com.baiheplayer.bbs.inter.INetCallBack;
import com.baiheplayer.bbs.utils.HttpsUtils;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;


import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * 首页的热点资讯
 *
 * Created by Administrator on 2016/12/31.
 */
@ContentView(R.layout.fragment_child)
public class ChildFragmentNews extends BaseFragment {
    @ViewInject(R.id.recycler_view)
    private RecyclerView mRecyclerView;

    private Context context ;
    private List<Article> articles;
    private ArticleAdapter adapter;

    private long lastFreshTime = 0;

    @Override
    public void getData() {
        context = getContext();
        articles = new ArrayList<>();
    }

    @Override
    public void onView() {
        adapter = new ArticleAdapter(context, articles, new ImageClickListenerImpl(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerView.setAdapter(adapter);

        getCurrentPage(new FlushCallback() {
            @Override
            public void onSuccess() {

            }
        });
    }

    private void getCurrentPage( final FlushCallback callback) {
        // TODO: 2017/2/15 需要检查断网
        RequestParams params = new RequestParams(HttpDatas.HOT_NEWS);
        params.addParameter("time",lastFreshTime);  //lastFreshTime
        HttpsUtils.getInstance().requestSavely(params, ArticleBack.class, new INetCallBack() {
            @Override
            public <T> void back(String type, T resultInfo) {
                ArticleBack data = (ArticleBack)resultInfo;
                if(data.getCode() == 0){
                    articles.addAll(data.getList());
                }
                getlastTime(articles);
                adapter.notifyDataSetChanged();
                callback.onSuccess();    //也不用管成功没有，到了就行
            }

            @Override
            public void fail(boolean isOnCallBack) {
                Snackbar.make(mRecyclerView,"网络错误",Snackbar.LENGTH_SHORT).show();
                callback.onSuccess();    //也不用管成功没有，到了就行
            }
        });
    }

    /**
     * 下拉刷新
     */
    public void refresh(FlushCallback callback){
        articles.clear();
        lastFreshTime = 0;
        getCurrentPage(callback);
    }

    /**
     * 上拉加载
     */
    public void loadMore(FlushCallback callback){
        getCurrentPage(callback);
    }

    //获取最后一条数据的时间
    private void getlastTime(List<Article> list){

        if(list.size()>0){
            int size = list.size();
            lastFreshTime = list.get(size-1).getDate();
        } else {
            lastFreshTime =0;
        }
    }

}
